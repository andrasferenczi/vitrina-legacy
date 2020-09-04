package stoyck.vitrina.domain.util

import android.content.Context
import java.io.File

fun getImagesCacheFolder(context: Context): File {
    return File(context.cacheDir, "temporary_images").also {
        it.mkdir()
    }
}