package stoyck.vitrina.domain.usecase

import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.persistence.VitrinaPersistence
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadSettingsUseCase @Inject constructor(
    private val persistence: VitrinaPersistence
) {

    suspend operator fun invoke(): PreferencesData {
        return PreferencesData(
            isOver18 = persistence.over18,
            shuffle = persistence.shuffle
        )
    }

}