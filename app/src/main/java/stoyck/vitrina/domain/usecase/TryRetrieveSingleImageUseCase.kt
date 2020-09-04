package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.data.RedditPost
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryRetrieveSingleImageUseCase @Inject constructor(
    private val retrieveNextPost: TryRetrieveNextPostAndMarkAsShownUseCase,
    private val deleteAllImagesFromCache: DeleteAllImagesFromCacheUseCase
) {

    data class Result(
        val originalPost: RedditPost,
        val imageLocation: File
    )

    suspend operator fun invoke(): Result? {
        val post = retrieveNextPost()
            ?: return null

        deleteAllImagesFromCache()
        
        // Todo: load image, save to cache and return it
        val image =
    }

}