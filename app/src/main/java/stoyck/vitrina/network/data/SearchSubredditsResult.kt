package stoyck.vitrina.network.data

data class SearchSubredditsResult(
    val subreddits: List<SearchSubredditsEntry> = emptyList()
)