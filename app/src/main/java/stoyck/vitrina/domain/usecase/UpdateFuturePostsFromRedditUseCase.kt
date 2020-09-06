package stoyck.vitrina.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateFuturePostsFromRedditUseCase @Inject constructor(
    private val saveFuturePostsUseCase: SaveFuturePostsUseCase,
    private val retrieveLatestImagesUseCase: RetrieveLatestImagesUseCase
) {

    suspend operator fun invoke() {
        val futurePosts = retrieveLatestImagesUseCase()
        saveFuturePostsUseCase(futurePosts)
    }

}