package stoyck.vitrina.domain.usecase

import android.net.Uri
import stoyck.vitrina.util.VResult
import java.io.File
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveArtworkOnDiskUseCase @Inject constructor(

) {

    data class Params(
        val existingFile: File,
        val uri: Uri,
        val title: String,
        val byLine: String,
        val attribution: String
    )

    suspend operator fun invoke(params: Params): VResult<File> {
        return VResult.failure(RuntimeException("Test"))
    }

}