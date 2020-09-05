package stoyck.vitrina.persistence

import android.content.Context
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.booleanPref
import hu.autsoft.krate.gson.gsonPref
import hu.autsoft.krate.intPref
import stoyck.vitrina.persistence.data.PersistedFuturePosts
import stoyck.vitrina.persistence.data.PersistedPostData
import stoyck.vitrina.persistence.data.PersistedSubredditData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitrinaPersistence
@Inject constructor(
    context: Context
) : SimpleKrate(context) {

    companion object {
        private val INITIAL_SUBREDDITS: List<PersistedSubredditData> = listOf(
            PersistedSubredditData(
                id = "2sbq3",
                name = "EarthPorn",
                minUpvoteCount = 1200
            ),
            PersistedSubredditData(
                id = "2scjs",
                name = "CityPorn",
                minUpvoteCount = 145
            ),
            PersistedSubredditData(
                id = "2s9jc",
                name = "spaceporn",
                minUpvoteCount = 580
            )
        )

        const val SUBREDDITS_KEY = "subreddits"
    }

    var shuffle by booleanPref("shuffle", false)

    var over18 by booleanPref("over18", false)

    var minimumImageWidth by intPref("minimumImageWidth", 100)
    var minimumImageHeight by intPref("minimumImageHeight", 100)

    var subreddits by gsonPref(SUBREDDITS_KEY, INITIAL_SUBREDDITS)

    var previousPosts by gsonPref<List<PersistedPostData>>("posts", emptyList())

    /**
     * Potential posts will be loaded into this cache
     *
     * This way when a single post is needed, it will not be necessary to query
     * all data from every subreddit (e.g.: when user has set their settings to shuffle)
     */
    var futurePostsCache by gsonPref(
        "future_posts",
        PersistedFuturePosts.default()
    )
}