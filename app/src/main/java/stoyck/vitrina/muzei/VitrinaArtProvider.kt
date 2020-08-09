package stoyck.vitrina.muzei

import android.util.Log
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import stoyck.vitrina.BuildConfig

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

//        if (initial) {
//            addArtwork(
//                Artwork.Builder()
//                    .token("initial")
//                    .title("Vitrina Starry")
//                    .byline("Vitrina Gogh, 20202.\nMuzei shows a new painting every day.")
//                    .attribution("wikiart.org")
//                    .persistentUri(Uri.parse("file:///android_asset/starrynight.jpg"))
//                    .webUri(Uri.parse("http://www.wikiart.org/en/vincent-van-gogh/the-starry-night-1889"))
//                    .build()
//            )
//        } else {
//            // Delete all but the latest artwork to avoid
//            // cycling through all of the previously Featured Art
//            query(contentUri, null, null, null, null)
//                .use { cursor ->
//                    if (BuildConfig.DEBUG) {
//                        Log.d(TAG, "Found ${cursor.count} existing artwork")
//                    }
//
//                    // Has to have at least 2
//                    if (cursor.count <= 1) {
//                        return@use
//                    }
//
//                    val moved = cursor.moveToFirst()
//                    if (!moved) {
//                        return@use
//                    }
//
//                    val baseColumnIndex = cursor.getColumnIndex(BaseColumns._ID)
//                    val baseValue = cursor.getString(baseColumnIndex)
//
//                    val count = delete(
//                        contentUri, BaseColumns._ID + " != ?",
//                        arrayOf(baseValue)
//                    )
//
//                    if (BuildConfig.DEBUG) {
//                        Log.d(TAG, "Deleted $count")
//                    }
//                }
//        }
    }
}