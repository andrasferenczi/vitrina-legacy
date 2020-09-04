package stoyck.vitrina.domain.usecase

import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.persistence.VitrinaPersistence
import stoyck.vitrina.persistence.data.PersistedFuturePosts
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveFuturePostsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(newData: PersistedFuturePosts) {
        persistence.futurePostsCache = newData
    }

}