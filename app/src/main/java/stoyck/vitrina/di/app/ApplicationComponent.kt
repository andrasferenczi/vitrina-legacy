package stoyck.vitrina.di.app

import dagger.Component
import stoyck.vitrina.MainActivity
import stoyck.vitrina.di.modules.NetworkModule
import stoyck.vitrina.muzei.VitrinaArtWorker
import stoyck.vitrina.muzei.VitrinaCommandReceiver
import stoyck.vitrina.muzei.commands.ArtworkSaveWorker
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        NetworkModule::class
    ]
)
interface ApplicationComponent {
    fun inject(activity: MainActivity)

    fun inject(worker: VitrinaArtWorker)

    fun inject(worker: ArtworkSaveWorker)

    fun inject(receiver: VitrinaCommandReceiver)
}