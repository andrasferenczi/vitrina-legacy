package stoyck.vitrina

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import stoyck.vitrina.di.TestVitrinaApplication
import stoyck.vitrina.network.RedditService
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class NetworkCallTests {

    @Inject
    lateinit var redditService: RedditService

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
    fun subredditRetrievalWorks() {
        val subreddit = runBlocking {
            redditService.retrieveSubreddit("earthporn")
        }

        // Capitalization is fixed
        Assert.assertEquals(subreddit.displayName, "EarthPorn")
    }

    @Test
    fun postsRetrievalWorks() {
        val subreddit = runBlocking {
            redditService.retrieveImagePosts("earthporn")
        }

        // Capitalization is fixed
        Assert.assertEquals(subreddit.size, 10)
    }

    @Test
    fun subredditRecommendationRetrievalWorks() {
        val subreddit = runBlocking {
            redditService.retrieveHints("earthporn")
        }

        // Capitalization is fixed
        Assert.assertEquals(subreddit.size, 2)
    }
}