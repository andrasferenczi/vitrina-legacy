package stoyck.vitrina.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SubredditSuggestion(
    @SerializedName("display_name")
    @Expose
    val displayName: String = "",

    @SerializedName("display_name_prefixed")
    @Expose
    val displayNamePrefixed: String = "",

    @Expose
    val title: String = "",

    @SerializedName("header_img")
    @Expose
    val headerImage: String = "",

    @SerializedName("icon_img")
    @Expose
    val iconImage: String = "",

    @SerializedName("public_description")
    @Expose
    val publicDescription: String = "",

    @Expose
    val description: String = "",

    @Expose
    val over18: Boolean = false
)