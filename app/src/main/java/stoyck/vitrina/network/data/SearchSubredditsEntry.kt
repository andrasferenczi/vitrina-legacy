package stoyck.vitrina.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchSubredditsEntry(
    @SerializedName("active_user_count")
    @Expose
    val activeUserCount: Long,

    @SerializedName("icon_img")
    @Expose
    val iconImg: String,

    @SerializedName("key_color")
    @Expose
    val keyColor: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("subscriber_count")
    @Expose
    val subscriberCount: Long,

    @SerializedName("is_chat_post_feature_enabled")
    @Expose
    val isChatPostFeatureEnabled: Boolean,

    @SerializedName("allow_chat_post_creation")
    @Expose
    val allowChatPostCreation: Boolean,

    @SerializedName("allow_images")
    @Expose
    val allowImages: Boolean
)