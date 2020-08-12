package stoyck.vitrina.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun openPermissions(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}
