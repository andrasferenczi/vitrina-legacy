package stoyck.vitrina.di.app

import dagger.Module
import dagger.Provides
import stoyck.vitrina.VitrinaApplication
import javax.inject.Singleton

/**
 * Not actually needed, just in case
 */
@Module
class ApplicationModule(
    private val app: VitrinaApplication
) {

    @Provides
    @Singleton
    fun provideApp(): VitrinaApplication = app

}