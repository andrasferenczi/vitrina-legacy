package stoyck.vitrina.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RedditAbout(
    @Expose
    val over18: Boolean = false,
    @Expose
    val description: String = "",

    @SerializedName("display_name")
    @Expose
    val displayName: String = "",
    @Expose
    val title: String = "",
    @Expose
    val id: String = "",

    @SerializedName("icon_img")
    @Expose
    val iconImg: String = "",

    @SerializedName("audience_target")
    @Expose
    val audienceTarget: String = "",

    @SerializedName("public_description")
    @Expose
    val publicDescription: String = ""
)