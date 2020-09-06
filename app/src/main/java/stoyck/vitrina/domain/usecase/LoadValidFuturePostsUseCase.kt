package stoyck.vitrina.domain.usecase

import stoyck.vitrina.persistence.data.PersistedFuturePosts
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadValidFuturePostsUseCase @Inject constructor(
    private val loadSubredditsUseCase: LoadSubredditsUseCase,
    private val loadFuturePostsUseCase: LoadFuturePostsUseCase,
    private val updateFuturePostsFromRedditUseCase: UpdateFuturePostsFromRedditUseCase
) {

    suspend operator fun invoke(): PersistedFuturePosts {
        val posts = loadFuturePostsUseCase()
        val subreddits = loadSubredditsUseCase()

        val subredditIds = subreddits.map { it.id }.toSet()
        val retrievedIds = posts.futurePostsBySubredditId.keys

        if (subredditIds != retrievedIds) {
            updateFuturePostsFromRedditUseCase()
            return this()
        }

        return posts
    }

}