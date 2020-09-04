package stoyck.vitrina.muzei

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.ProviderClient
import com.google.android.apps.muzei.api.provider.ProviderContract
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.TryRetrieveNextMuzeiArtworkUseCase
import java.util.concurrent.Executors
import javax.inject.Inject

class VitrinaArtWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val TAG = "VitrinaWorker"

        private val SINGLE_THREAD_CONTEXT by lazy {
            Executors.newSingleThreadExecutor { target -> Thread(target, "VitrinaArt") }
                .asCoroutineDispatcher()
        }

        internal fun enqueueLoad(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(
                OneTimeWorkRequestBuilder<VitrinaArtWorker>()
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
        }

    }

    @Inject
    lateinit var tryRetrieveNextMuzeiArtwork: TryRetrieveNextMuzeiArtworkUseCase

    init {
        (context.applicationContext as VitrinaApplication)
            .appComponent
            .inject(this)
    }

//    private suspend fun tryRetrieveNextArtwork(): Pair<Artwork, File>? {
//        val singleImage = tryRetrieveNextMuzeiArtwork()
//            ?: return null
//
//        return singleImage.
//    }

    private suspend fun updatePictures(
        provider: ProviderClient,
        newPictureCount: Int,
        extraImagesInMuzei: Int
    ) {
        val totalImages = newPictureCount + extraImagesInMuzei

        for (i in 0 until newPictureCount) {
            val (artwork, file) = tryRetrieveNextMuzeiArtwork() ?: break

            // todo: remove last
            provider.addArtwork(artwork)?.also {
                applicationContext.contentResolver.openOutputStream(it)?.use { output ->
                    file.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    override suspend fun doWork(): Result = withContext(SINGLE_THREAD_CONTEXT) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Started doing work")
        }

        val provider = ProviderContract
            .getProviderClient(
                applicationContext,
                BuildConfig.VITRINA_AUTHORITY
            )

        updatePictures(provider, 3, 10)

//        // keep the last one as first when setting
//        val latest = provider.lastAddedArtwork
//
//        val prev = provider.readArtworks()
//
//        val newArtworks =
//            if (latest == null)
//                artworks
//            else
//                listOf(latest, *artworks.toTypedArray())
//
////        provider.addArtwork(listOf()).map {
////            provider.context.contentResolver.openOutputStream(it).use {
////
////            }
////        }
//
//        provider.setArtwork(newArtworks)

        return@withContext Result.success()
    }
}