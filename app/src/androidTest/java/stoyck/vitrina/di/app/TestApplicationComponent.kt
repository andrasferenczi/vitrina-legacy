package stoyck.vitrina.di.app

import dagger.Component
import stoyck.vitrina.NetworkCallTests
import stoyck.vitrina.SubredditPersistenceTest
import stoyck.vitrina.UseCaseTests
import stoyck.vitrina.ViewModelTest
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

    fun inject(test: UseCaseTests)

    fun inject(test: ViewModelTest)

    fun inject(test: SubredditPersistenceTest)
}