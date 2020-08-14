package stoyck.vitrina.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RedditPost(
    @SerializedName("domain")
    @Expose
    val domain: String = "",
    @SerializedName("subreddit")
    @Expose
    val subreddit: String = "",
    @SerializedName("id")
    @Expose
    val id: String = "",
    @SerializedName("title")
    @Expose
    val title: String = "",
    @SerializedName("num_crossposts")
    @Expose
    val numCrossposts: Long = 0,
    @SerializedName("score")
    @Expose
    val score: Long = 0,
    @SerializedName("over_18")
    @Expose
    val over18: Boolean = false,
    @SerializedName("subreddit_id")
    @Expose
    val subredditId: String = "",
    @SerializedName("post_hint")
    @Expose
    val postHint: PostHint? = null,
    @SerializedName("stickied")
    @Expose
    val stickied: Boolean = false,
    @SerializedName("name")
    @Expose
    val name: String = "",
    @SerializedName("permalink")
    @Expose
    val permalink: String = "",
    @SerializedName("subreddit_type")
    @Expose
    val subredditType: String = "",
    @SerializedName("created")
    @Expose
    val created: Long = 0L,
    @SerializedName("url")
    @Expose
    val url: String = "",
    @SerializedName("author")
    @Expose
    val author: String = "",
    @SerializedName("created_utc")
    @Expose
    val createdUtc: Long = 0L,
    @SerializedName("subreddit_name_prefixed")
    @Expose
    val subredditNamePrefixed: String = "",
    @SerializedName("ups")
    @Expose
    val ups: Long = 0L,
    @SerializedName("num_comments")
    @Expose
    val numComments: Long = 0L,
    @SerializedName("is_video")
    @Expose
    val isVideo: Boolean = false
)