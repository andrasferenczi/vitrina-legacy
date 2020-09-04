package stoyck.vitrina.persistence.data

import stoyck.vitrina.network.data.RedditPost

/**
 * Could be a typealias, but I prefer type
 *
 * Posts by Id
 *
 * NO check that depends on deeper info on the settings:
 * - minimum upvote count
 * - nsfw check
 *
 */
data class PersistedFuturePosts(
    val futurePostsBySubredditId: Map<String, List<RedditPost>>
) {

    fun postIdsRemoved(postIds: Set<String>): PersistedFuturePosts {
        return postsFiltered { it.id !in postIds }
    }

    fun postsFiltered(by: (post: RedditPost) -> Boolean): PersistedFuturePosts {
        // Not very effective, but whatever
        return PersistedFuturePosts(
            futurePostsBySubredditId
                .map { (key, value) ->
                    return@map key to value.filter(by)
                }
                .toMap()
        )
    }

    companion object {

        fun default(): PersistedFuturePosts {
            return PersistedFuturePosts(emptyMap())
        }

    }

}