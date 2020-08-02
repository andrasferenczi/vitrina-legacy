package stoyck.vitrina.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class BearerTokenInterceptor(
    private val getBearerToken: ()-> String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getBearerToken()

        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}