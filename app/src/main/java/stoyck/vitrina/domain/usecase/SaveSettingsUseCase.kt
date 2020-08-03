package stoyck.vitrina.domain.usecase

import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.persistence.VitrinaPersistence
import stoyck.vitrina.persistence.data.PersistedPostData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveSettingsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(preferences: PreferencesData) {
        persistence.over18 = preferences.isOver18
        persistence.shuffle = preferences.shuffle
    }

}