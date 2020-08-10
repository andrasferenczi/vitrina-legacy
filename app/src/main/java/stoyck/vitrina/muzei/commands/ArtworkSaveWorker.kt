package stoyck.vitrina.muzei.commands

import android.content.Context
import android.widget.Toast
import androidx.work.*
import com.google.gson.Gson
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import stoyck.vitrina.R
import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.domain.usecase.SaveArtworkOnDiskUseCase
import java.util.concurrent.Executors
import javax.inject.Inject

class ArtworkSaveWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val KEY_PARAMS = "KEY_PARAMS"

        private val gson = Gson()

        private val SINGLE_THREAD_CONTEXT by lazy {
            Executors.newSingleThreadExecutor { target -> Thread(target, "VitrinaArt") }
                .asCoroutineDispatcher()
        }

        internal fun enqueueLoad(
            context: Context,
            params: SaveArtworkOnDiskUseCase.Params
        ) {
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(
                OneTimeWorkRequestBuilder<ArtworkSaveWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(KEY_PARAMS, gson.toJson(params))
                            .build()
                    )
                    .build()
            )
        }
    }

    @Inject
    lateinit var saveArtworkOnDiskUseCase: SaveArtworkOnDiskUseCase

    init {
        (context.applicationContext as VitrinaApplication)
            .appComponent
            .inject(this)
    }

    override suspend fun doWork(): Result = withContext(SINGLE_THREAD_CONTEXT) {
        Toast.makeText(
            applicationContext,
            R.string.action_save_worker_start,
            Toast.LENGTH_LONG
        ).show()

        val paramsRaw = inputData.getString(KEY_PARAMS)

        if (paramsRaw == null) {
            // todo: log
            return@withContext Result.failure()
        }

        val params =
            gson.fromJson(paramsRaw, SaveArtworkOnDiskUseCase.Params::class.java)

        saveArtworkOnDiskUseCase(params)

        Result.success()
    }
}