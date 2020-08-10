package stoyck.vitrina.muzei

import android.util.Log
import androidx.core.app.RemoteActionCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.R

class VitrinaArtProvider : MuzeiArtProvider() {

    companion object {
        private const val TAG = "VitrinaArtProvider"
    }

    override fun onLoadRequested(initial: Boolean) {
        val context = context ?: return
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onLoadRequested: (initial: $initial)")
        }

        // This is the important part
        VitrinaArtWorker.enqueueLoad(context)
    }

    override fun getCommandActions(artwork: Artwork): List<RemoteActionCompat> {
        val context = context ?: return emptyList()

        val existingCommands = super.getCommandActions(artwork)

        return listOf(
            *existingCommands.toTypedArray(),
            RemoteActionCompat(
                IconCompat.createWithResource(context, R.drawable.ic_save),
                context.getString(R.string.action_save_title),
                context.getString(R.string.action_save_description),
                VitrinaCommandReceiver.createPendingIntent(
                    context,
                    artwork,
                    VitrinaCommandReceiver.Companion.VitrinaCommand.Save
                )
            )
        )
    }

}