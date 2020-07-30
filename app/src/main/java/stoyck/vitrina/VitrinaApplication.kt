package stoyck.vitrina

import android.app.Application
import stoyck.vitrina.di.app.ApplicationComponent
import stoyck.vitrina.di.app.DaggerApplicationComponent

open class VitrinaApplication : Application() {

    // DaggerApplicationComponent is a generated class
    // that implements ApplicationComponent
    open val appComponent: ApplicationComponent =
    // No injection in the application
    // that would make this `var` instead of `val`
        // and then the derived could not specify a subtype
        DaggerApplicationComponent.builder()
            .build()
}