package stoyck.vitrina

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import stoyck.vitrina.di.TestVitrinaApplication
import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.domain.usecase.RetrieveLatestImagesUseCase
import stoyck.vitrina.domain.usecase.SaveSettingsUseCase
import stoyck.vitrina.domain.usecase.SaveSubredditsUseCase
import stoyck.vitrina.persistence.data.PersistedSubredditData
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class UseCaseTests {

    @Inject
    lateinit var saveSubreddits: SaveSubredditsUseCase

    @Inject
    lateinit var saveSettings: SaveSettingsUseCase

    @Inject
    lateinit var retrieveImages: RetrieveLatestImagesUseCase

    @Before
    fun setup() {
        val app =
            InstrumentationRegistry.getInstrumentation()
                .targetContext
                .applicationContext
                    as TestVitrinaApplication

        app.appComponent.inject(this)

        runBlocking {
            saveSettings(
                PreferencesData(
                    isOver18 = false,
                    shuffle = false
                )
            )
        }
    }

    private fun saveSubredditByNames(names: List<String>) {
        runBlocking {
            saveSubreddits(names.map {
                PersistedSubredditData.fromName(it)
            })
        }
    }

    @Test
    fun retrievingInOrder() {
        val subreddits = listOf(
            "humanporn",
            "earthporn"
        )

        runBlocking {
            saveSubredditByNames(subreddits)
        }
    }

    @Test
    fun subreddithWithNoImages() {
        val subreddits = listOf("jokes")

        val posts = runBlocking {
            saveSubredditByNames(subreddits)

            retrieveImages()
        }

        Assert.assertEquals(0, posts.size)
    }

    @Test
    fun subreddithWithOnlyImages() {
        val subreddits = listOf("tinder")

        val posts = runBlocking {
            saveSubredditByNames(subreddits)

            retrieveImages()
        }

        Assert.assertTrue("Most of the posts should be images", posts.size > 50)
    }


}