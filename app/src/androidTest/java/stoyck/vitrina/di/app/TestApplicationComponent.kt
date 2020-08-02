package stoyck.vitrina.di.app

import dagger.Component
import stoyck.vitrina.NetworkCallTests
import stoyck.vitrina.di.modules.LoggerModule
import stoyck.vitrina.di.modules.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class,
        LoggerModule::class
    ]
)
interface TestApplicationComponent : ApplicationComponent {
    fun inject(test: NetworkCallTests)
}