package stoyck.vitrina.util

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class DebouncedTextWatcher(
    delayMillis: Long,
    private val debouncedAction: (text: String) -> Unit
) : TextWatcher, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val debouncer = Debouncer(
        delayMillis = delayMillis
    )

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = s.toString().trim()

        debouncer {
            debouncedAction(searchText)
        }
    }

    override fun afterTextChanged(s: Editable?) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}