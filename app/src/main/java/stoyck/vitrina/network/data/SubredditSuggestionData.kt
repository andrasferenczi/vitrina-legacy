package stoyck.vitrina.network.data

data class SubredditSuggestionData(
    val before: String? = null,
    val after: String? = null,
    val children: List<SubredditSuggestionChild> = emptyList()
)