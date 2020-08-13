package stoyck.vitrina.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun uiThread(action: suspend () -> Unit) {
    withContext(Dispatchers.Main) {
        action()
    }
}


suspend fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_LONG) {
    uiThread {
        Toast.makeText(
            context,
            text,
            duration
        ).show()
    }
}


suspend fun showToast(context: Context, stringRes: Int, duration: Int = Toast.LENGTH_LONG) {
    uiThread {
        Toast.makeText(
            context,
            stringRes,
            duration
        ).show()
    }
}