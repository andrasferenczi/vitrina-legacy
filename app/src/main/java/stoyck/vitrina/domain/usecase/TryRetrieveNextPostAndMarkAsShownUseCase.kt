package stoyck.vitrina.domain.usecase

import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.persistence.data.PersistedSubredditData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryRetrieveNextPostAndMarkAsShownUseCase @Inject constructor(
    private val loadSubredditsUseCase: LoadSubredditsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val loadValidFuturePostsUseCase: LoadValidFuturePostsUseCase,
    private val markPostsAsShownUseCase: MarkPostsAsShownUseCase,
    private val updateFuturePostsFromRedditUseCase: UpdateFuturePostsFromRedditUseCase
) {

    private suspend fun loadSubredditsOrdered(settings: PreferencesData): List<PersistedSubredditData> {
        val subreddits = loadSubredditsUseCase()

        if (settings.shuffle) {
            return subreddits.shuffled()
        }

        return subreddits
    }

    suspend operator fun invoke(
        retryCount: Int = 1
    ): RedditPost? {
        if (retryCount < 0) {
            return null
        }
        val settings = loadSettingsUseCase()
        val subreddits = loadSubredditsOrdered(settings)

        val futurePosts = loadValidFuturePostsUseCase()

        val nextImage = subreddits
            // optimized
            .asSequence()
            // The given cache
            .map { subreddit ->
                val posts = futurePosts.futurePostsBySubredditId[subreddit.id]
                    ?.filter { post -> post.score > subreddit.minUpvoteCount }

                val isOver18Disabled = !settings.isOver18
                if (isOver18Disabled) {
                    posts?.filterNot { post -> post.over18 }
                } else {
                    posts
                }
            }
            // not likely to happen, because it has been validated
            // to contain something for each id
            .filterNotNull()
            .flatMap { it.asSequence() }
            // just one is needed
            .firstOrNull()

        if (nextImage == null) {
            // Retry happens if it is out of subreddits
            // Worst case is that loadValidFuturePostsUseCase() fetches,
            // does not find anything and then reloads

            // It is important that the list of next post is not necessarily empty
            // it can happen that it is full of bad posts that are below upvote count
            updateFuturePostsFromRedditUseCase()

            return this(retryCount - 1)
        }

        markPostsAsShownUseCase(listOf(nextImage))

        return nextImage
    }

}