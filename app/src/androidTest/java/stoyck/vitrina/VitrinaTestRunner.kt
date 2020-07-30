package stoyck.vitrina

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import stoyck.vitrina.di.TestVitrinaApplication

/**
 * Used in build.gradle
 *
 * testInstrumentationRunner "stoyck.vitrina.VitrinaTestRunner"
 */
class VitrinaTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestVitrinaApplication::class.java.name, context)
    }
}