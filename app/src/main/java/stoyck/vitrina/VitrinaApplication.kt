package stoyck.vitrina

import android.app.Application
import stoyck.vitrina.di.app.ApplicationComponent

/**
 * No generics - out type is fine
 */
abstract class VitrinaApplication : Application() {

    abstract val appComponent: ApplicationComponent

}