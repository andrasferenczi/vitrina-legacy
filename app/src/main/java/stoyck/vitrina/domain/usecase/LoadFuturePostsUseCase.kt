package stoyck.vitrina.domain.usecase

import stoyck.vitrina.persistence.VitrinaPersistence
import stoyck.vitrina.persistence.data.PersistedFuturePosts
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadFuturePostsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(): PersistedFuturePosts = persistence.futurePostsCache

}