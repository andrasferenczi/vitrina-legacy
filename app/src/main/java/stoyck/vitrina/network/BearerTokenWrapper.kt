package stoyck.vitrina.network

import stoyck.vitrina.network.data.RedditAuthorizationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BearerTokenWrapper @Inject constructor() {

    val isTokenValid: Boolean
        get() = this.expiresAtMillis > System.currentTimeMillis()

    private var expiresAtMillis: Long = 0

    var authorization: RedditAuthorizationResponse? = null
        set(value) {
            expiresAtMillis = if (value == null) {
                0L
            } else {
                // subtracting some value to make sure that the api does not get called in the end
                // this is overkill, but whatever
                val expiresInSeconds = 0L.coerceAtLeast(value.expiresIn - 100)
                val expiresInMillis = expiresInSeconds * SECONDS_IN_MILLIS

                System.currentTimeMillis() + expiresInMillis
            }

            field = value
        }

    val token: String?
        get() = authorization?.accessToken

    companion object {
        private const val SECONDS_IN_MILLIS = 1000
    }

}