package stoyck.vitrina.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


// https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java?page=1&tab=votes#tab-top
fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    // Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = this.currentFocus
    // If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
}