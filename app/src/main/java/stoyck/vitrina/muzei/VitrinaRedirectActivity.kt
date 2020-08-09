package stoyck.vitrina.muzei

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.google.android.apps.muzei.api.MuzeiContract
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.R

/**
 * Copied from UnsplashRedirectActivity
 */

/**
 * This activity's sole purpose is to redirect users to Muzei, which is where they should
 * activate Muzei and then select the Unsplash source.
 *
 * You'll note the usage of the `enable_launcher` boolean resource value to only enable
 * this on API 29+ devices as it is on API 29+ that a launcher icon becomes mandatory for
 * every app.
 */
class VitrinaRedirectActivity : ComponentActivity() {

    companion object {
        private const val TAG = "VitrinaRedirect"
        private const val MUZEI_PACKAGE_NAME = "net.nurik.roman.muzei"
        private const val MUZEI_PLAY_STORE_LINK =
            "https://play.google.com/store/apps/details?id=$MUZEI_PACKAGE_NAME"
    }

    private val requestLauncher = registerForActivityResult(StartActivityForResult()) {
        // It doesn't matter what the result is, the important part is that the
        // user hit the back button to return to this activity. Since this activity
        // has no UI of its own, we can simply finish the activity.
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if Vitrina is already selected
        val launchIntent = packageManager.getLaunchIntentForPackage(MUZEI_PACKAGE_NAME)

        if (launchIntent != null) {
            val isProviderSelected =
                MuzeiContract.Sources.isProviderSelected(this, BuildConfig.VITRINA_AUTHORITY)

            if (isProviderSelected) {
                // Already selected so just open Muzei
                requestLauncher.launch(launchIntent)
                return
            }
        }

        // Build the list of Intents plus the Toast message that should be displayed
        // to users when we successfully launch one of the Intents
        val intents = listOf(
            MuzeiContract.Sources.createChooseProviderIntent(BuildConfig.VITRINA_AUTHORITY)
                    to R.string.message_enable_vitrina,
            launchIntent
                    to R.string.message_enable_vitrina_source,
            Intent(Intent.ACTION_VIEW).setData(Uri.parse(MUZEI_PLAY_STORE_LINK))
                    to R.string.message_muzei_missing_error
        )

        // Go through each Intent/message pair, trying each in turn
        val success = intents.fold(false) { success, (intent, message) ->
            if (success) {
                // If one launch has succeeded, we don't need to
                // try any further Intents
                return@fold success
            }

            if (intent == null) {
                // A null Intent means there's nothing to attempt to launch
                return@fold false
            }

            try {
                requestLauncher.launch(intent)
                // Only if the launch succeeds do we show the Toast and trigger success
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                true
            } catch (e: Exception) {
                Log.v(TAG, "Intent $intent failed", e)
                false
            }
        }

        if (!success) {
            // Only if all Intents failed do we show a 'everything failed' Toast
            Toast.makeText(this, R.string.message_play_store_missing_error, Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }
}