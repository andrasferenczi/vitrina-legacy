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
import kotlinx.coroutines.delay
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

    // For Q:
    // https://stackoverflow.com/questions/56904485/how-to-save-an-image-in-android-q-using-mediastore/56990305
    // For other:
    // https://stackoverflow.com/questions/8560501/android-save-image-into-gallery
    private fun copyFile(
        file: File,
        fileName: String
    ): Uri {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

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

        val uri = copyFile(file, name)
        uiThread { showSavedImage(uri) }

        showToast(context, R.string.message_image_saved_successfully)
    }

}