package stoyck.vitrina.domain.preferences

data class PreferencesData(
    val isOver18: Boolean,
    val shuffle: Boolean,
    val minimumImageWidth: Int,
    val minimumImageHeight: Int
)
