package stoyck.vitrina.muzei

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import stoyck.vitrina.BuildConfig
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.RetrieveLatestImagesUseCase
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.network.data.fullPostLink
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
    lateinit var loadImagesUseCase: RetrieveLatestImagesUseCase

    init {
        (context.applicationContext as VitrinaApplication)
            .appComponent
            .inject(this)
    }

    private fun RedditPost.toArtwork(): Artwork {
        return Artwork(
            token = id,
            title = subredditNamePrefixed,
            byline = title,
            attribution = author,
            persistentUri = Uri.parse(url),
            webUri = Uri.parse(fullPostLink)
        )
    }

    override suspend fun doWork(): Result = withContext(SINGLE_THREAD_CONTEXT) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Started doing work")
        }

        val posts = loadImagesUseCase()
        val artworks = posts.map { it.toArtwork() }.take(4)
        val provider = ProviderContract
            .getProviderClient(applicationContext, BuildConfig.VITRINA_AUTHORITY)

        // keep the last one as first when setting
        val latest = provider.lastAddedArtwork

        val newArtworks =
            if (latest == null)
                artworks
            else
                listOf(latest, *artworks.toTypedArray())

        provider.setArtwork(newArtworks)

        return@withContext Result.success()
    }
}