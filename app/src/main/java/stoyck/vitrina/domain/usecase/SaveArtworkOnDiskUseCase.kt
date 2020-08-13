package stoyck.vitrina.domain.usecase

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.R
import stoyck.vitrina.util.FileUtil
import stoyck.vitrina.util.openPermissions
import stoyck.vitrina.util.showToast
import stoyck.vitrina.util.uiThread
import java.io.File
import java.io.IOException
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

    private fun showSavedImage(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.VITRINA_AUTHORITY,
            file
        )

        showSavedImage(uri)
    }

    private fun showSavedImage(uri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Some devices need that apparently
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "image/*")

        context.startActivity(intent)
    }

    private suspend fun ensureStoragePermission() {
        val isPermissionDenied = context.checkCallingOrSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        if (isPermissionDenied) {
            showToast(context, R.string.message_enable_storage_permission)

            Log.d("vitrina", "Waiting")
            delay(1000L)
            openPermissions(context)
        }
    }

    // https://stackoverflow.com/questions/56904485/how-to-save-an-image-in-android-q-using-mediastore/56990305
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun copyFile(
        file: File,
        fileName: String
    ): Uri {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        // contentValues.put(MediaStore.MediaColumns.MIME_TYPE, )
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        val resolver = context.contentResolver

        var uri: Uri? = null

        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
                ?: throw IOException("Failed to create new MediaStore record.")

            val outputStream = resolver.openOutputStream(uri)
                ?: throw IOException("Failed to get output stream.");

            FileUtil.copy(file, outputStream)

            return uri
        } catch (e: Exception) {
            uri?.let {
                resolver.delete(it, null, null)
            }

            throw e
        }
    }

    suspend fun copyFileLegacy(
        file: File,
        fileName: String
    ): File {
        val newFile = File(getImagesDirectory(), fileName)

        if (!newFile.exists()) {
            newFile.createNewFile()
            FileUtil.copy(file, newFile)
        } else {
            val alreadyExistsMessage =
                context.getString(R.string.message_file_already_exists_at)

            showToast(context, alreadyExistsMessage.format(newFile.path))
        }

        return newFile
    }

    suspend operator fun invoke(params: Params) {
        // If not ui thread, this might run earlier than previous toast calls
        uiThread {
            ensureStoragePermission()
        }

        val file = params.existingFile

        val baseFileName = (params.title + "_" + params.byLine.take(20) + "_" + params.attribution)
            .toLowerCase(Locale.ROOT)
            .trim()
            .replace(Regex("[^a-zA-Z0-9\\\\.\\\\-]"), "_")

        val name = "$baseFileName.jpg"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = copyFile(file, name)
            uiThread { showSavedImage(uri) }
        } else {
            val newFile = copyFileLegacy(file, name)
            uiThread { showSavedImage(newFile) }
        }

        showToast(context, R.string.message_image_saved_successfully)
    }

}