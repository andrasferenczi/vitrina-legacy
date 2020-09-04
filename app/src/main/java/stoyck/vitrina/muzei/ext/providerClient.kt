package stoyck.vitrina.muzei.ext

import android.content.Context
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderClient

fun ProviderClient.readArtworks(context: Context): List<Artwork> {
    return context.contentResolver
        .query(contentUri, null, null, null, null)
        .use { cursor ->

            if (cursor == null) {
                return@use emptyList()
            }

            val result = mutableListOf<Artwork>()

            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                val artwork = Artwork.fromCursor(cursor)
                result.add(artwork)
                cursor.moveToNext()
            }

            return@use result
        }
}