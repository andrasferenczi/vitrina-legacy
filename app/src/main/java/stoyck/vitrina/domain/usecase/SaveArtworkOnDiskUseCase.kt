package stoyck.vitrina.domain.usecase

import android.Manifest
import android.R.attr.content
import android.R.attr.mimeType
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.R
import stoyck.vitrina.util.*
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

    private fun saveImageToFile() {
        val relativeLocation = Environment.DIRECTORY_PICTURES

        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)

        val resolver = context.contentResolver

        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(contentUri, contentValues)
            ?: throw IOException("Failed to create new MediaStore record.")

        val stream = resolver.openOutputStream(uri)
            ?: throw IOException("Failed to get output stream.");

        stream.use {

        }
    }

    suspend operator fun invoke(params: Params): VResult<File> {
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

        val newFile = File(getImagesDirectory(), name)

        try {
            if (!newFile.exists()) {
                newFile.createNewFile()
                FileUtil.copy(file, newFile)
            } else {
                val alreadyExistsMessage =
                    context.getString(R.string.message_file_already_exists_at)

                showToast(context, alreadyExistsMessage.format(newFile.path))
            }

            uiThread {
                showSavedImage(newFile)
            }
        } catch (e: Exception) {
            return VResult.failure(e)
        }

        return VResult.success(newFile)
    }

}