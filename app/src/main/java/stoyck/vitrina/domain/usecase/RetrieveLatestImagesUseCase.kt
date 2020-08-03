package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.RedditService
import stoyck.vitrina.network.data.PostHint
import stoyck.vitrina.network.data.RedditPost
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrieveLatestImagesUseCase @Inject constructor(
    private val loadPosts: LoadPostsUseCase,
    private val loadSettings: LoadSettingsUseCase,
    private val loadSubreddits: LoadSubredditsUseCase,
    private val reddit: RedditService
) {

    suspend operator fun invoke(): List<RedditPost> {
        val previousPosts = loadPosts()
        val existingPostIds = previousPosts.map { it.id }.toSet()

        val subreddits = loadSubreddits()
        val preferences = loadSettings()

        val subredditNames = subreddits.map { it.name }

        val posts = if (preferences.shuffle) {
            reddit.retrievePosts(subredditNames)
        } else {
            subredditNames.flatMap {
                // Todo: rate limit these calls, or reddit will do it
                reddit.retrievePosts(it)
            }
        }

        val minUpvoteCounts =
            subreddits.associateBy(
                { it.name.toLowerCase(Locale.ROOT) },
                { it.minUpvoteCount }
            )

        val images = posts
            .asSequence()
            // for easy indexing
            .map { it.copy(subreddit = it.subreddit.toLowerCase(Locale.ROOT)) }
            .filterImages()
            .filter { it.id !in existingPostIds }
            .filter {
                val minUpvoteCount = minUpvoteCounts[it.subreddit] ?: return@filter false
                return@filter minUpvoteCount < it.score
            }
            .map { convertToDirectImageLink(it) }
            .filterNotNull()
            .toList()
            .take(100)

        return images
    }

    private fun Sequence<RedditPost>.filterImages(): Sequence<RedditPost> {
        return this
            .filter { !it.stickied }
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
            PostHint.Other -> null
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