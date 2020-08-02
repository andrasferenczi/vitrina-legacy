package stoyck.vitrina.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import stoyck.vitrina.network.data.RedditAboutPage
import stoyck.vitrina.network.data.RedditPostPage
import stoyck.vitrina.network.data.SearchSubredditsResult

interface RedditOauthApi {

    @GET("/r/{subreddit}/about.json")
    suspend fun getAbout(
        @Path("subreddit") subreddit: String
    ): RedditAboutPage

    @GET("/r/{subreddit}.json")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int? = null
    ): RedditPostPage

    @POST("/api/search_subreddits.json")
    suspend fun searchSubreddits(
        @Query("query") query: String,
        @Query("exact") exact: Boolean? = null,
        @Query("include_over_18") includeOver18: Boolean? = null,
        @Query("include_unadvertisable") includeUnadvertisable: Boolean? = null,
        @Query("typeahead_active") sort: Boolean? = null
    ): SearchSubredditsResult

}