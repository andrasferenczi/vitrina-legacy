package stoyck.vitrina.network

import android.util.Base64
import androidx.annotation.VisibleForTesting
import stoyck.vitrina.network.data.RedditAbout
import stoyck.vitrina.network.data.RedditAuthorizationResponse
import stoyck.vitrina.network.data.RedditPost
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class RedditService @Inject constructor(
    private val redditApi: RedditApi,
    @Named("reddit_client_id")
    private val clientId: String
) {
    private val usernameAndPassword = "$clientId: "

    private val authorization: String =
        "Basic " + Base64.encodeToString(
            usernameAndPassword.toByteArray(),
            Base64.DEFAULT
        ).trim() // remove the goddamn new line at the end


    @VisibleForTesting
    suspend fun retrieveAccessToken(): RedditAuthorizationResponse {
        return redditApi.getAccessToken(authorization = authorization)
    }

    suspend fun retrieveSubreddit(subreddit: String): RedditAbout {
        val page = redditApi.getAbout(subreddit = subreddit)
        return page.data
    }

    suspend fun retrieveImagePosts(subreddit: String): List<RedditPost> {
        val result = redditApi.getPosts(
            subreddit = subreddit,
            limit = 50
        )

        val posts = result.data.children.map { it.data }

        return posts
            .asSequence()
            .filter { !it.stickied }
            .filter { it.subreddit.equals(subreddit, ignoreCase = true) }
            .toList()
    }

    suspend fun retrieveHints(query: String): List<String> {
        val result = redditApi.searchRedditNames(query = query)
        return result.names
    }

}