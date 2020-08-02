package stoyck.vitrina.network

import retrofit2.http.*
import stoyck.vitrina.network.data.RedditAboutPage
import stoyck.vitrina.network.data.RedditAuthorizationResponse
import stoyck.vitrina.network.data.RedditPostPage
import stoyck.vitrina.network.data.SubredditSuggestionPage

/**
 * Endpoints that are reachable without any token
 */
interface RedditPublicApi {

    @FormUrlEncoded
    @POST("/api/v1/access_token")
    suspend fun getAccessToken(
        @Header("authorization") authorization: String,
        @Field("grant_type") grantType: String = "https://oauth.reddit.com/grants/installed_client",
        @Field("device_id") deviceId: String = "DO_NOT_TRACK_THIS_DEVICE"
    ): RedditAuthorizationResponse

    @Deprecated("Please use the oauth endpoint")
    @GET("/r/{subreddit}/about.json")
    suspend fun getAbout(
        @Path("subreddit") subreddit: String
    ): RedditAboutPage

    @Deprecated("Please use the oauth endpoint")
    @GET("/r/{subreddit}.json")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int? = null
    ): RedditPostPage

    @Deprecated("Please use the oauth endpoint")
    @GET("/subreddits.json")
    suspend fun getSubreddits(
        @Query("q") query: String,
        @Query("limit") limit: Int? = null,
        @Query("count") count: Int? = null,
        @Query("sort") sort: String = "relevance"
    ): SubredditSuggestionPage
}