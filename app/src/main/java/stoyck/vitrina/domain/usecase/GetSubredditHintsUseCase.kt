package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.RedditService
import stoyck.vitrina.ui.SubredditSuggestionData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSubredditHintsUseCase @Inject constructor(
    private val network: RedditService
) {

    suspend operator fun invoke(partialSubredditName: String): List<SubredditSuggestionData> {
        return network.retrieveHints(partialSubredditName)
    }

}