package stoyck.vitrina

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import stoyck.vitrina.di.TestVitrinaApplication
import stoyck.vitrina.domain.usecase.LoadSubredditsUseCase
import stoyck.vitrina.domain.usecase.SaveSubredditsUseCase
import stoyck.vitrina.domain.usecase.TryAddSubredditUseCase
import stoyck.vitrina.persistence.VitrinaPersistence
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SubredditPersistenceTest {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var loadSubredditUseCase: LoadSubredditsUseCase

    @Inject
    lateinit var saveSubredditsUseCase: SaveSubredditsUseCase

    @Inject
    lateinit var tryAddSubredditsUseCase: TryAddSubredditUseCase

    @Before
    fun setup() {
        val app =
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .applicationContext
                    as TestVitrinaApplication
        app.appComponent.inject(this)
    }

    @Test
    fun test_1__clearAndLoadDefaultSubreddits() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(VitrinaPersistence.SUBREDDITS_KEY)
            .commit()

        val subreddits = runBlocking {
            loadSubredditUseCase()
        }

        Assert.assertArrayEquals(
            arrayOf("EarthPorn", "CityPorn", "spaceporn"),
            subreddits.map { it.name }.toTypedArray()
        )
    }

    @Test
    fun test_2__tryAddNewSubreddit() {
        val newSubredditName = "flutterdev"

        val (addedSubreddit, newSubreddits) = runBlocking {
            // Should work, if not that is outside the scope of the test
            // maybe reddit api is down
            tryAddSubredditsUseCase(newSubredditName).getOrNull()!!
        }

        Assert.assertEquals(
            newSubredditName,
            addedSubreddit.name.toLowerCase()
        )

        Assert.assertArrayEquals(
            arrayOf(newSubredditName, "earthporn", "cityporn", "spaceporn"),
            newSubreddits.map { it.name.toLowerCase() }.toTypedArray()
        )

        // And it is also the same if retrieved directly
        val retrievedDirectly = runBlocking { loadSubredditUseCase() }
        Assert.assertArrayEquals(
            newSubreddits.toTypedArray(),
            retrievedDirectly.toTypedArray()
        )
    }


}