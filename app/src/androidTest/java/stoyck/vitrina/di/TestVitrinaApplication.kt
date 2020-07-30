package stoyck.vitrina.di

import stoyck.vitrina.VitrinaApplication
import stoyck.vitrina.di.app.DaggerTestApplicationComponent
import stoyck.vitrina.di.app.TestApplicationComponent

class TestVitrinaApplication : VitrinaApplication() {

    override val appComponent: TestApplicationComponent =
        DaggerTestApplicationComponent.builder()
            .build()
}