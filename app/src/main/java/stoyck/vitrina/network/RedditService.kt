package stoyck.vitrina.network

import stoyck.vitrina.network.data.RedditAbout
import stoyck.vitrina.network.data.RedditPost
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditService @Inject constructor(
    private val redditApi: RedditApi
) {

    suspend fun retrieveSubreddit(subreddit: String): RedditAbout {
        val page = redditApi.getAbout(subreddit = subreddit)
        return page.data
    }

    suspend fun retrieveImagePosts(subreddit: String, limit: Int): List<RedditPost> {
        val result = redditApi.getPosts(
            subreddit = subreddit,
            limit = limit
        )

        TODO()
    }

}