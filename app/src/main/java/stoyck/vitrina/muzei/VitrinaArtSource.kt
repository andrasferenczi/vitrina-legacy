package stoyck.vitrina.muzei

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * This class is kept only to serve as a tombstone to Muzei to know to replace it
 * with [VitrinaArtProvider].
 */
class VitrinaArtSource : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}