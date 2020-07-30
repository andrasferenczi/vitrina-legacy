package stoyck.vitrina.di.app

import dagger.Component
import stoyck.vitrina.MainActivity
import stoyck.vitrina.di.modules.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    NetworkModule::class
])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
}