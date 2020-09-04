package stoyck.vitrina.domain.usecase

import android.content.Context
import stoyck.vitrina.domain.util.getImagesCacheFolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAllImagesFromCacheUseCase @Inject constructor(
    private val context: Context
) {

    suspend operator fun invoke() {
        val folder = getImagesCacheFolder(context)

        folder.listFiles()?.forEach { it.delete() }
    }

}