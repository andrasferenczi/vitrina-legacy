package stoyck.vitrina.persistence.data

import androidx.annotation.VisibleForTesting
import stoyck.vitrina.network.data.RedditAbout

/**
 * Don't change this if possible
 */
data class PersistedSubredditData(
    val id: String,
    val name: String,
    val minUpvoteCount: Long
) {

    companion object {
        fun fromAbout(about: RedditAbout): PersistedSubredditData {
            return PersistedSubredditData(
                id = about.id,
                name = about.displayName,
                minUpvoteCount = 10
            )
        }

        @VisibleForTesting
        fun fromName(name: String, minUpvoteCount: Long = 0): PersistedSubredditData {
            return PersistedSubredditData(
                id = "NO_ID",
                minUpvoteCount = minUpvoteCount,
                name = name
            )
        }
    }

}
