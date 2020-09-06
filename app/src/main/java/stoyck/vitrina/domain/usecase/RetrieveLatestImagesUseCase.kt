package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.RedditService
import stoyck.vitrina.network.data.PostHint
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.persistence.data.PersistedFuturePosts
import stoyck.vitrina.persistence.data.PersistedSubredditData
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrieveLatestImagesUseCase @Inject constructor(
    private val loadPosts: LoadPostsUseCase,
    private val loadSubreddits: LoadSubredditsUseCase,
    private val reddit: RedditService
) {

    private suspend fun loadPostsFromAllSubreddits(
        subreddits: List<PersistedSubredditData>
    ): PersistedFuturePosts {
        val posts = subreddits.map {
            // Todo: rate limit these calls, or reddit will do it
            val posts = reddit.retrievePosts(it.name)

            return@map it.id to posts
        }.toMap()

        return PersistedFuturePosts(posts)
    }

    /**
     * Return type is used as the future posts to make sure the key is the id
     */
    suspend operator fun invoke(): PersistedFuturePosts {
        val previousPosts = loadPosts()
        val subreddits = loadSubreddits()

        val existingPostIds = previousPosts.map { it.id }.toSet()

        @Suppress("UnnecessaryVariable")
        val posts = loadPostsFromAllSubreddits(subreddits)
            .postIdsRemoved(existingPostIds)
            .processImages()

        return posts
    }

    private fun PersistedFuturePosts.processImages(): PersistedFuturePosts {
        return PersistedFuturePosts(
            this.futurePostsBySubredditId
                .map { (key, value) ->
                    key to value.asSequence().filterImages().toList()
                }
                .toMap()
        )
    }

    private fun Sequence<RedditPost>.filterImages(): Sequence<RedditPost> {
        return this
            .filter { !it.stickied }
            .map { convertToDirectImageLink(it) }
            .filterNotNull()
        // this was needed for some reason, cannot be used for multiple subreddits so directly
        // .filter { it.subreddit.equals(subreddit, ignoreCase = true) }
    }

    private fun convertToDirectImageLink(info: RedditPost): RedditPost? {
        return when (info.postHint) {
            PostHint.Image -> {
                if (info.url.isBlank())
                    null
                else
                    info.copy()
            }
            PostHint.Link -> {
                if (info.domain.contains("imgur"))
                    return info.copy(
                        url = convertImgurLinkToDirectLink(info.url),
                        postHint = PostHint.Image
                    )
                else
                    null
            }
            null, PostHint.Other -> null
        }
    }


    private fun convertImgurLinkToDirectLink(imgurUrl: String): String {
        // Example:
        // https://imgur.com/CZKRusY
        // https://i.imgur.com/CZKRusY.jpg

        if (!imgurUrl.toLowerCase(Locale.ROOT).contains("imgur")) {
            throw IllegalArgumentException("The link provided is not an imgur link: $imgurUrl")
        }

        val id = imgurUrl
            .substringAfterLast("com/")
            // There might be a dot in there -
            // this way it can convert direct links to direct link without a problem
            .substringBefore(".")

        if (id == imgurUrl) {
            throw IllegalArgumentException("Could not extract id from the imgur url.")
        } else {
            // Can be jpg, imgur will solve this
            return "https://i.imgur.com/$id.jpg"
        }
    }
}