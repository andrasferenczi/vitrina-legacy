package stoyck.vitrina.domain.usecase

import stoyck.vitrina.persistence.VitrinaPersistence
import stoyck.vitrina.persistence.data.PersistedPostData
import stoyck.vitrina.persistence.data.PersistedSubredditData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadSubredditsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(): List<PersistedSubredditData> = persistence.subreddits

}