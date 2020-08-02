package stoyck.vitrina.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import stoyck.vitrina.network.data.*

interface RedditApi {

    @FormUrlEncoded
    @POST("/api/v1/access_token")
    suspend fun getAccessToken(
        @Header("authorization") authorization: String,
        @Field("grant_type") grantType: String = "https://oauth.reddit.com/grants/installed_client",
        @Field("device_id") deviceId: String = "DO_NOT_TRACK_THIS_DEVICE"
    ): RedditAuthorizationResponse

    @GET("/r/{$SUBREDDIT}/about.json")
    suspend fun getAbout(
        @Path(SUBREDDIT) subreddit: String
    ): RedditAboutPage

    @GET("/r/{$SUBREDDIT}.json")
    suspend fun getPosts(
        @Path(SUBREDDIT) subreddit: String,
        @Query("limit") limit: Int? = null
    ): RedditPostPage

    @GET("/subreddits.json")
    suspend fun getSubreddits(
        @Query("q") query: String,
        @Query("limit") limit: Int? = null,
        @Query("count") count: Int? = null,
        @Query("sort") sort: String = "relevance"
    ): SubredditSuggestionPage

    @GET("/api/search_reddit_names.json")
    suspend fun searchRedditNames(
        @Query("query") query: String,
        @Query("exact") exact: Boolean? = null,
        @Query("include_over_18") includeOver18: Boolean? = null,
        @Query("include_unadvertisable") includeUnadvertisable: Boolean? = null,
        @Query("typeahead_active") sort: Boolean? = null
    ): SearchRedditNamesResult

    companion object {
        private const val SUBREDDIT = "subreddit"

        fun create(
            interceptors: List<Interceptor>,
            modifier: (builder: Retrofit.Builder) -> Unit = {}
        ): RedditApi {
            val client: OkHttpClient = OkHttpClient.Builder().apply {
                interceptors.forEach {
                    this.addInterceptor(it)
                }
            }.build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(client)
                .apply(modifier)
                .build()

            return retrofit.create(RedditApi::class.java)
        }
    }

}