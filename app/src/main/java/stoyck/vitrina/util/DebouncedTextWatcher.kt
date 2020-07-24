package stoyck.vitrina.util

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DebouncedTextWatcher(
    private val delayMillis: Long,
    private val debouncedAction: (text: String) -> Unit
) : TextWatcher, CoroutineScope {

    private var searchFor = ""

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = s.toString().trim()
        if (searchText == searchFor)
            return

        searchFor = searchText

        launch {
            delay(delayMillis)  //debounce timeOut
            if (searchText != searchFor)
                return@launch

            debouncedAction(searchText)
        }
    }

    override fun afterTextChanged(s: Editable?) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}