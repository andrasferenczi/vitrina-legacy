package stoyck.vitrina.domain.usecase

import android.content.Context
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import stoyck.vitrina.util.VResult
import stoyck.vitrina.util.showToast
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveArtworkOnDiskUseCase @Inject constructor(
    private val context: Context
) {

    data class Params(
        val existingFile: File,
        val uri: Uri,
        val title: String,
        val byLine: String,
        val attribution: String
    )

    private fun getImagesDirectory(): File {
        // Returns the directory the user definitely cannot find :(
        //return Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES
        //)
        return File("/storage/emulated/0/Vitrina").apply { mkdirs() }
    }

    suspend operator fun invoke(params: Params): VResult<File> {
        val file = params.existingFile

        val baseFileName = (params.title + "_" + params.byLine.take(20) + "_" + params.attribution)
            .toLowerCase(Locale.ROOT)
            .trim()
            .replace(Regex("[^a-zA-Z0-9\\\\.\\\\-]"), "_")

        val name = "$baseFileName.jpg"

        showToast(context, name)

//        val newFile = File(getImagesDirectory(), name)
//
//        if (!newFile.exists()) {
//            newFile.createNewFile()
//            FileUtil.copy(file, newFile)
//
//        }

        return VResult.failure(RuntimeException("Test"))
    }

}