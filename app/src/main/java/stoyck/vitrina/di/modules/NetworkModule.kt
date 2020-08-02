package stoyck.vitrina.di.modules

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import stoyck.vitrina.network.RedditApi
import stoyck.vitrina.network.UserAgentInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    companion object {

        @Provides
        @IntoSet
        @JvmStatic
        @Singleton
        fun provideUserAgentInterceptor(
            @Named("package_name")
            packageName: String
        ) = UserAgentInterceptor(packageName)

        @Provides
        @JvmStatic
        @Singleton
        fun provideRedditApi(
            interceptors: Set<@JvmSuppressWildcards Interceptor>
        ): RedditApi {
            return RedditApi.create(interceptors.toList())
        }

    }

}