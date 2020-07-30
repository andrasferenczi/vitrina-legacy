package stoyck.vitrina.network.data

data class RedditPostData(
    val before: String? = null,
    val after: String? = null,
    val children: List<RedditPostChild> = emptyList()
)