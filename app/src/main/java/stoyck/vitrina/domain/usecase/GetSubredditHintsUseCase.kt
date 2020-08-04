package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.RedditService
import stoyck.vitrina.ui.SubredditSuggestionData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSubredditHintsUseCase @Inject constructor(
    private val network: RedditService,
    private val loadSettings: LoadSettingsUseCase
) {

    suspend operator fun invoke(partialSubredditName: String): List<SubredditSuggestionData> {
        val settings = loadSettings()

        return network.retrieveHints(
            query = partialSubredditName,
            includeOver18 = settings.isOver18
        )
    }

}