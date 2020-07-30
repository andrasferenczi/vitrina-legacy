package stoyck.vitrina.di.modules

import dagger.Module
import dagger.Provides
import stoyck.vitrina.network.RedditApi
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    companion object {

        @Provides
        @JvmStatic
        @Singleton
        fun provideRedditApi(): RedditApi {
            return RedditApi.create()
        }

    }

}