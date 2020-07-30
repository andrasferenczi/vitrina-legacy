package stoyck.vitrina.network.data

import com.google.gson.annotations.SerializedName

enum class PostHint {
    @SerializedName("image")
    Image,
    @SerializedName("link")
    Link,
    // Not sure if this exists
    @SerializedName("other")
    Other
}