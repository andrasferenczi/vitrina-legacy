package stoyck.vitrina.util

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext

class Debouncer(
    private val delayMillis: Long = 300L
): CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val index = AtomicLong(0)

    operator fun invoke(action: () -> Unit) {
        val currentIndex = index.incrementAndGet()

        launch {
            delay(delayMillis)

            if (currentIndex != index.get()) {
                return@launch
            }

            action()
        }
    }

}