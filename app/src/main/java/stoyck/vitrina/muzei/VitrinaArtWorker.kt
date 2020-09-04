package stoyck.vitrina.muzei

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.ProviderContract
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.TryRetrieveNextMuzeiArtworkUseCase
import stoyck.vitrina.muzei.ext.readArtworks
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
    lateinit var tryRetrieveNextArtworkUseCase: TryRetrieveNextMuzeiArtworkUseCase

    init {
        (context.applicationContext as VitrinaApplication)
            .appComponent
            .inject(this)
    }

    override suspend fun doWork(): Result = withContext(SINGLE_THREAD_CONTEXT) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Started doing work")
        }

        val artworks = tryRetrieveNextArtworkUseCase()
        val provider = ProviderContract
            .getProviderClient(
                applicationContext,
                BuildConfig.VITRINA_AUTHORITY
            )

        // keep the last one as first when setting
        val latest = provider.lastAddedArtwork

        val prev = provider.readArtworks()

        val newArtworks =
            if (latest == null)
                artworks
            else
                listOf(latest, *artworks.toTypedArray())

//        provider.addArtwork(listOf()).map {
//            provider.context.contentResolver.openOutputStream(it).use {
//
//            }
//        }

        provider.setArtwork(newArtworks)

        return@withContext Result.success()
    }
}