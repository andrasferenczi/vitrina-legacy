package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.persistence.data.PersistedPostData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkPostsAsShownUseCase @Inject constructor(
    private val loadFuturePostsUseCase: LoadFuturePostsUseCase,
    private val saveFuturePostsUseCase: SaveFuturePostsUseCase,
    private val loadPostsUseCase: LoadPostsUseCase,
    private val savePostsUseCase: SavePostsUseCase
) {

    suspend operator fun invoke(
        redditPosts: List<RedditPost>
    ) {
        val posts = redditPosts.map { PersistedPostData(id = it.id, imageLink = it.url) }

        updatePersistedPosts(newPosts = posts)

        val ids = posts.map { it.id }.toSet()
        updateFuturePosts(ids)
    }

    private suspend fun updatePersistedPosts(newPosts: List<PersistedPostData>) {
        val previousPosts = loadPostsUseCase()

        val allPosts = listOf(
            *previousPosts.toTypedArray(),
            *newPosts.toTypedArray()
        )
            // do not save thousands of posts - bad for serialization
            .takeLast(200)

        savePostsUseCase(allPosts)
    }

    private suspend fun updateFuturePosts(idsToRemove: Set<String>) {
        val prev = loadFuturePostsUseCase()

        val next = prev.postIdsRemoved(idsToRemove)
        saveFuturePostsUseCase(next)
    }

}