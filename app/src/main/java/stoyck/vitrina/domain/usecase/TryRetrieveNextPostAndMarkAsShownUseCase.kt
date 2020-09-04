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
    private val markPostsAsShownUseCase: MarkPostsAsShownUseCase
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

        @Suppress("UnnecessaryVariable")
        val nextImage = subreddits
            // optimized
            .asSequence()
            // The given cache
            .map { subreddit ->
                val posts = futurePosts.futurePostsBySubredditId[subreddit.id]
                    ?.filter { post -> post.score > subreddit.minUpvoteCount }

                if (settings.isOver18) {
                    posts?.filter { post -> !post.over18 }
                } else {
                    posts
                }
            }
            // not likely to happen, because it has been validated
            // to contain something for each id
            .filterNotNull()
            // list can be empty for each subreddit
            .map { it.firstOrNull() }
            // because of earlier line
            .filterNotNull()
            // just one is needed
            .firstOrNull()
        // Retry happens if it is out of subreddits
        // Worst case is that loadValidFuturePostsUseCase() fetches,
        // does not find anything and then reloads
            ?: return this(retryCount - 1)

        markPostsAsShownUseCase(listOf(nextImage))

        return nextImage
    }

}