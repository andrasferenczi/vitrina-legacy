package stoyck.vitrina.network

import stoyck.vitrina.network.data.RedditAbout
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.network.data.SubredditSuggestion
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

    suspend fun retrieveImagePosts(subreddit: String): List<RedditPost> {
        val result = redditApi.getPosts(
            subreddit = subreddit,
            limit = 50
        )

        val posts = result.data.children.map { it.data }

        return posts
            .asSequence()
            .filter { !it.stickied }
            .filter {  it.subreddit.equals(subreddit, ignoreCase = true)}
            .toList()
    }

    suspend fun retrieveHints(query: String): List<SubredditSuggestion> {
        val result = redditApi.getSubreddits(query = query)
        return result.data.children.map { it.data }
    }

}