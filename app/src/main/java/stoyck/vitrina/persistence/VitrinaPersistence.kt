package stoyck.vitrina.persistence

import android.content.Context
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.booleanPref
import hu.autsoft.krate.gson.gsonPref
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

    var subreddits by gsonPref(SUBREDDITS_KEY, INITIAL_SUBREDDITS)

    var previousPosts by gsonPref<List<PersistedPostData>>("posts", emptyList())
}