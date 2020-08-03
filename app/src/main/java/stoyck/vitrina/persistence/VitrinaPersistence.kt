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

    var shuffle by booleanPref("shuffle", false)

    var over18 by booleanPref("over18", false)

    var subreddits by gsonPref<List<PersistedSubredditData>>("subreddits", emptyList())

    var previousPosts by gsonPref<List<PersistedPostData>>("posts", emptyList())
}