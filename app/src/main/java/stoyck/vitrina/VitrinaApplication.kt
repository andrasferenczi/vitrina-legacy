package stoyck.vitrina

import android.app.Application
import stoyck.vitrina.di.app.DaggerApplicationComponent

class VitrinaApplication : Application() {

    // DaggerApplicationComponent is a generated class
    // that implements ApplicationComponent
    val appComponent = DaggerApplicationComponent.create()

}