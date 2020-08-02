package stoyck.vitrina

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.gson.gsonPref
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenceMigrationTest {

    companion object {
        private const val SINGLE_PREFERENCE_KEY = "testing_key_value"
    }

    private data class StateA(val value: String) {
        companion object {
            fun createDefault() = StateA(value = "StateA")
        }
    }

    // As if the class was moved - trying to parse the same value as a different class
    private data class StateAMoved(val value: String) {
        companion object {
            fun createDefault() = StateAMoved(value = "StateAMoved")
        }
    }

    private data class StateAExtendedNoDefault(val value: String, val isGood: Boolean) {
        companion object {
            fun createDefault() =
                StateAExtendedNoDefault(value = "StateAExtendedNoDefault", isGood = true)
        }
    }

    private data class StateAExtendedWithDefault(val value: String, val isGood: Boolean = true) {
        companion object {
            fun createDefault() = StateAExtendedWithDefault(value = "StateAExtendedWithDefault")
        }
    }

    private data class StateARenamed(val bestValue: String) {
        companion object {
            fun createDefault() = StateARenamed(bestValue = "StateARenamed")
        }
    }

    // This is an idea but it does not work with data class
    // https://stackoverflow.com/a/53929404/4420543
    // when the time comes to extend, create a field where the current version is saved
    // and migrate manually if it does not match
    private data class StateAStringProperExtend(
        val value: String,
        val secondValue: String? = null
    ) {
        // no tests are needed, this only describes what actually happens
    }

    // ###

    private class Manager(context: Context) : SimpleKrate(context) {
        var v1: StateA? by gsonPref(SINGLE_PREFERENCE_KEY)

        var v2: StateAMoved? by gsonPref(SINGLE_PREFERENCE_KEY)

        var v3: StateAExtendedNoDefault? by gsonPref(SINGLE_PREFERENCE_KEY)

        var v4: StateAExtendedWithDefault? by gsonPref(SINGLE_PREFERENCE_KEY)

        var v5: StateARenamed? by gsonPref(SINGLE_PREFERENCE_KEY)
    }

    //
    lateinit var context: Context

    private lateinit var manager: Manager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext

        this.manager = Manager(context)

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(SINGLE_PREFERENCE_KEY)
            .commit()
    }

    @Test
    fun firstStart() {
        Assert.assertEquals(null, manager.v1)

        manager.v1 = StateA.createDefault()
        Assert.assertEquals(StateA.createDefault(), manager.v1)
    }

    @Test
    fun movingClass() {
        Assert.assertEquals(null, manager.v1)

        val v1 = StateA.createDefault()
        manager.v1 = v1

        val v2 = manager.v2

        Assert.assertEquals(
            StateAMoved(value = v1.value),
            v2
        )
    }

    @Test
    fun extendWithoutDefault() {
        Assert.assertEquals(null, manager.v1)

        val v1 = StateA.createDefault()
        manager.v1 = v1

        val v3 = manager.v3

        Assert.assertEquals(
            // gson initalizes with a default value in kotlin style
            StateAExtendedNoDefault(value = v1.value, isGood = false),
            v3
        )
    }

    @Test
    fun extendWithDefault() {
        Assert.assertEquals(null, manager.v1)

        val v1 = StateA.createDefault()
        manager.v1 = v1

        val v4 = manager.v4

        Assert.assertEquals(
            // kotlin default value is not taken into account
            StateAExtendedWithDefault(value = v1.value, isGood = false),
            v4
        )
    }

    @Test
    fun renameVariableInsideState() {
        Assert.assertEquals(null, manager.v1)

        val v1 = StateA.createDefault()
        manager.v1 = v1

        val v5 = manager.v5

        Assert.assertEquals(
            // when extending with a string, expect null
            null,
            v5!!.bestValue
        )
    }


}