package stoyck.vitrina

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import stoyck.vitrina.di.TestVitrinaApplication
import stoyck.vitrina.domain.MainViewModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ViewModelTest {

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var context: Context

    @Before
    fun setup() {
        val app =
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .applicationContext
                    as TestVitrinaApplication
        app.appComponent.inject(this)
    }

    private suspend fun <T> LiveData<T>.observeUntilChange(
        skip: Int
    ): T? {

        return suspendCoroutine {

            var remaining = skip

            val observer = object : Observer<T> {
                override fun onChanged(t: T?) {
                    remaining--

                    if (remaining < 0) {
                        it.resume(t)
                        this@observeUntilChange.removeObserver(this)
                    }

                }
            }
            observeForever(observer)
        }
    }

    private fun <T> LiveData<T>.observeBlockingUntilChange(
        skip: Int
    ): T? {
        return runBlocking {
            withContext(Dispatchers.Main) {
                this@observeBlockingUntilChange.observeUntilChange(skip)
            }
        }
    }

//    @Test
//    fun test_1__deleteAndRetrieveDefaultSubreddits() {
//        PreferenceManager.getDefaultSharedPreferences(context)
//            .edit()
//            .remove(VitrinaPersistence.SUBREDDITS_KEY)
//            .commit()
//
//        val subreddits =
//            viewModel.subreddits.observeBlockingUntilChange(skip = 1)
//
//        Assert.assertNotNull(subreddits)
//        Assert.assertArrayEquals(
//            arrayOf("EarthPorn", "CityPorn", "spaceporn"),
//            subreddits!!.map { it.name }.toTypedArray()
//        )
//    }

//    @Test
//    fun test_2__addNewSubreddit() {
//        viewModel.tryAddSubreddit("flutterdev")
//
//
//    }
}