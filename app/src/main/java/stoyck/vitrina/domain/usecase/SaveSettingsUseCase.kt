package stoyck.vitrina.domain.usecase

import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.persistence.VitrinaPersistence
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveSettingsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(preferences: PreferencesData) {
        persistence.over18 = preferences.isOver18
        persistence.shuffle = preferences.shuffle

        persistence.minimumImageWidth = preferences.minimumImageWidth
        persistence.minimumImageHeight = preferences.minimumImageHeight
    }

}