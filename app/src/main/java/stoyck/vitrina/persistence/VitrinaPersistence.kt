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

    val shuffle by booleanPref("shuffle", false)

    val over18 by booleanPref("over18", false)

    val subreddits by gsonPref<List<PersistedSubredditData>>("subreddits", emptyList())

    val previousPosts by gsonPref<List<PersistedPostData>>("posts", emptyList())
}