package stoyck.vitrina.muzei.commands

import stoyck.vitrina.BuildConfig

object VitrinaProtocolConstants {

    const val PREFIX = BuildConfig.APPLICATION_ID

    const val KEY_ARTWORK_ID = "$PREFIX.ARTWORK_ID"
    const val KEY_ARTWORK_DATA = "$PREFIX.ARTWORK_DATA"
    const val KEY_ARTWORK_URI = "$PREFIX.ARTWORK_URI"
    const val KEY_ARTWORK_TITLE = "$PREFIX.ARTWORK_TITLE"
    const val KEY_ARTWORK_BYLINE = "$PREFIX.ARTWORK_BYLINE"
    const val KEY_ARTWORK_ATTRIBUTION = "$PREFIX.ARTWORK_ATTRIBUTION"

    const val COMMAND_SAVE_KEY = "save"
    const val COMMAND_DELETE_FROM_MUZEI_KEY = "delete_from_muzei"
}