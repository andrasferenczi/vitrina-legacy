package stoyck.vitrina.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RedditAuthorizationResponse(
    // This is the important part
    @SerializedName("access_token")
    @Expose
    val accessToken: String,

    @SerializedName("token_type")
    @Expose
    val tokenType: String,

    // always DO_NOT_TRACK_THIS_DEVICE, as it is used like that
    @SerializedName("device_id")
    @Expose
    val deviceId: String,

    // always 3600 in my case, meaning 1 hour
    @SerializedName("expires_in")
    @Expose
    val expiresIn: Long,

    // always "*"
    @SerializedName("scope")
    @Expose
    val scope: String
)