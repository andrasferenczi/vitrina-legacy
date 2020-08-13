package stoyck.vitrina.muzei

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.gson.Gson
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.R
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.SaveArtworkOnDiskUseCase
import stoyck.vitrina.muzei.commands.ArtworkSaveWorker
import stoyck.vitrina.muzei.commands.VitrinaProtocolConstants
import java.io.File
import javax.inject.Inject

/**
 * Receiving the Intent triggered from the RemoteActionCompat.
 *
 * The whole point of this whole stuff is that it's difficult to call
 * a suspend function just from intent.
 *
 * Hence the intent -> receiver -> worker -> usecase call chain.
 * UseCase is the actual important one, everything else is boilerplate.
 */
class VitrinaCommandReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gson: Gson

    override fun onReceive(context: Context?, intent: Intent?) {
        fun quit(): Nothing {
            throw RuntimeException("Invalid onreceive ${intent.toString()}")
        }

        if (context == null || intent == null) {
            quit()
        }

        (context.applicationContext as VitrinaApplication)
            .appComponent
            .inject(this)

        val action = intent.action ?: quit()

        val extras = intent.extras ?: quit()

        val data = extras.getSerializable(VitrinaProtocolConstants.KEY_ARTWORK_DATA) as File?
        val uri = extras.getParcelable<Uri>(VitrinaProtocolConstants.KEY_ARTWORK_URI)
        val title = extras.getString(VitrinaProtocolConstants.KEY_ARTWORK_TITLE)
        val byLine = extras.getString(VitrinaProtocolConstants.KEY_ARTWORK_BYLINE)
        val attribution = extras.getString(VitrinaProtocolConstants.KEY_ARTWORK_ATTRIBUTION)

        if (
            data == null ||
            uri == null ||
            title == null ||
            byLine == null ||
            attribution == null
        ) {
            quit()
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "Debug: Received action $action", Toast.LENGTH_SHORT)
                .show()
        }

        when (action) {
            VitrinaProtocolConstants.COMMAND_SAVE_KEY -> {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(
                        context,
                        "Debug: " + context.getString(R.string.action_save_command_received),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                ArtworkSaveWorker.enqueueLoad(
                    context,
                    gson,
                    SaveArtworkOnDiskUseCase.Params(
                        existingFile = data,
                        uri = uri,
                        attribution = attribution,
                        title = title,
                        byLine = byLine
                    )
                )
            }
        }
    }

    companion object {

        enum class VitrinaCommand(val id: String) {
            Save(VitrinaProtocolConstants.COMMAND_SAVE_KEY);
        }

        fun createPendingIntent(
            context: Context,
            artwork: Artwork,
            command: VitrinaCommand
        ): PendingIntent {

            // Idea from MuzeiArtProvider
            val intent = Intent(context, VitrinaCommandReceiver::class.java).apply {
                this.action = command.id

                putExtra(VitrinaProtocolConstants.KEY_ARTWORK_DATA, artwork.data)
                putExtra(VitrinaProtocolConstants.KEY_ARTWORK_URI, artwork.persistentUri)
                putExtra(VitrinaProtocolConstants.KEY_ARTWORK_TITLE, artwork.title)
                putExtra(VitrinaProtocolConstants.KEY_ARTWORK_BYLINE, artwork.byline)
                putExtra(VitrinaProtocolConstants.KEY_ARTWORK_ATTRIBUTION, artwork.attribution)
            }

            return PendingIntent.getBroadcast(
                context,
                command.id.hashCode() + 1000 * artwork.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    }
}
