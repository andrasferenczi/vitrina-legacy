package stoyck.vitrina.muzei

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.ProviderClient
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.TryRetrieveNextMuzeiArtworkUseCase
import stoyck.vitrina.muzei.ext.pruneOldArtworks
import stoyck.vitrina.muzei.ext.retrieveVitrinaProviderClient
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

    private suspend fun loadNewArtworks(
        provider: ProviderClient,
        newPictureCount: Int
    ) {
        for (i in 0 until newPictureCount) {
            val (artwork, file) = tryRetrieveNextMuzeiArtwork() ?: break

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

        val provider = applicationContext.retrieveVitrinaProviderClient()

        loadNewArtworks(provider, 4)
        provider.pruneOldArtworks(applicationContext, 16)

        return@withContext Result.success()
    }
}