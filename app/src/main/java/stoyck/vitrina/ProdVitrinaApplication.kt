package stoyck.vitrina

import stoyck.vitrina.di.app.ApplicationComponent
import stoyck.vitrina.di.app.ApplicationModule
import stoyck.vitrina.di.app.DaggerApplicationComponent

/**
 * final - so that it does not leak `this` when creating dagger
 */
class ProdVitrinaApplication : VitrinaApplication() {

    // DaggerApplicationComponent is a generated class
    // that implements ApplicationComponent
    override val appComponent: ApplicationComponent =
    // No injection in the application
    // that would make this `var` instead of `val`
        // and then it would leak `this`
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
}