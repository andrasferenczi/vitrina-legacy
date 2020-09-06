package stoyck.vitrina.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import stoyck.vitrina.domain.util.getImagesCacheFolder
import stoyck.vitrina.network.data.RedditPost
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryRetrieveSingleImageUseCase @Inject constructor(
    private val context: Context,
    private val loadSettings: LoadSettingsUseCase,
    private val retrieveNextPost: TryRetrieveNextPostAndMarkAsShownUseCase,
    private val deleteAllImagesFromCache: DeleteAllImagesFromCacheUseCase
) {

    data class Result(
        val originalPost: RedditPost,
        val temporaryImageLocation: File
    )

    private fun loadBitmap(
        url: String
    ): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    }

    private fun saveImage(image: Bitmap, file: File) {
        file.outputStream().use { out ->
            image.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    suspend operator fun invoke(): Result? {
        val post = retrieveNextPost()
            ?: return null

        deleteAllImagesFromCache()

        val bitmap = loadBitmap(post.url)

        // Keep loading new posts until there is a good image
        // 'Next post' usecase saves what images it has already returned
        val preferences = loadSettings()
        if (bitmap.width < preferences.minimumImageWidth
            || bitmap.height < preferences.minimumImageHeight
        ) {
            return this()
        }

        val cachedImagesFolder = getImagesCacheFolder(context)
        val newImageFile = File(cachedImagesFolder, "${System.currentTimeMillis()}.png")

        saveImage(bitmap, newImageFile)

        return Result(post, newImageFile)
    }

}