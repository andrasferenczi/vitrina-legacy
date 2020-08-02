package stoyck.vitrina.di.modules

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import stoyck.vitrina.network.BearerTokenWrapper
import stoyck.vitrina.network.RedditOauthApi
import stoyck.vitrina.network.RedditPublicApi
import stoyck.vitrina.network.interceptors.BearerTokenInterceptor
import stoyck.vitrina.network.interceptors.UserAgentInterceptor
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger 2 info: either:
 * - class
 * - abstract class + companion object with @JvmStatic
 */
@Module
class NetworkModule {

    @Provides
    @Named("bearer_token_interceptor")
    @Singleton
    fun bearerTokenInterceptor(
        bearerTokenWrapper: BearerTokenWrapper
    ): BearerTokenInterceptor = BearerTokenInterceptor {
        bearerTokenWrapper.token ?: throw RuntimeException("No bearer token available")
    }

    @Provides
    @IntoSet
    @Singleton
    fun userAgentInterceptor(
        @Named("package_name")
        packageName: String
    ): Interceptor = UserAgentInterceptor(packageName)

    @Provides
    @Singleton
    fun baseRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.reddit.com")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    fun publicRedditApi(
        baseRetrofit: Retrofit,
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): RedditPublicApi {
        val retrofit = baseRetrofit
            .newBuilder()
            .client(createOkHttpClient(interceptors.toList()))
            .build()

        return retrofit.create(RedditPublicApi::class.java)
    }

    @Provides
    @Singleton
    fun oauthRedditApi(
        baseRetrofit: Retrofit,
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
        @Named("bearer_token_interceptor")
        bearerTokenInterceptor: BearerTokenInterceptor
    ): RedditOauthApi {
        val allInterceptors = setOf(
            *interceptors.toTypedArray(),
            bearerTokenInterceptor
        )

        val retrofit = baseRetrofit
            .newBuilder()
            .baseUrl("https://oauth.reddit.com")
            .client(createOkHttpClient(allInterceptors.toList()))
            .build()

        return retrofit.create(RedditOauthApi::class.java)
    }

    private companion object {

        fun createOkHttpClient(interceptors: List<Interceptor>): OkHttpClient {
            return OkHttpClient.Builder().apply {
                interceptors.forEach {
                    this.addInterceptor(it)
                }
            }.build()
        }

    }

}