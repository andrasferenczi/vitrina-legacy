package stoyck.vitrina.domain.usecase

import android.net.Uri
import com.google.android.apps.muzei.api.provider.Artwork
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.network.data.fullPostLink
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryRetrieveNextMuzeiArtworkUseCase @Inject constructor(
    private val tryRetrieveSingleImage: TryRetrieveSingleImageUseCase
) {

    data class Result(val artwork: Artwork, val temporaryFile: File)

    private fun RedditPost.toArtwork(): Artwork {
        return Artwork(
            token = id,
            title = subredditNamePrefixed,
            byline = title,
            attribution = author,
            persistentUri = Uri.parse(url),
            webUri = Uri.parse(fullPostLink)
        )
    }

    suspend operator fun invoke(): Result? {
        /**
         * Image retrieval can only be done one by one
         * This makes sure that cache size is always enough
         */

        val image = tryRetrieveSingleImage()
            ?: return null

        return Result(
            image.originalPost.toArtwork(),
            image.temporaryImageLocation
        )
    }

}