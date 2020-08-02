package stoyck.vitrina.persistence.fields

//import android.content.Context
//import android.content.SharedPreferences
//
//class VitrinaPrefs(val context: Context) {
//
//    private val sharedPreferences: SharedPreferences
//        get() = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
//
//
//}
//
//private class UnsaveableEditor(
//    editor: SharedPreferences.Editor
//) : SharedPreferences.Editor by editor {
//
//    override fun commit(): Boolean = throw RuntimeException("Not available")
//
//    override fun apply() = throw RuntimeException("Not available")
//}
//
//abstract class PrefField<T> constructor(
//    private val fieldName: String
//) {
//
//    abstract fun save(editor: SharedPreferences.Editor)
//
//    abstract fun retrieve(preferences: SharedPreferences): T
//
//}
//
//private class BooleanField(fieldName: String) : PrefField<Boolean>(fieldName) {
//
//    override fun save(editor: SharedPreferences.Editor) {
//        editor.putBoolean()
//    }
//
//    override fun retrieve(preferences: SharedPreferences): Boolean {
//        TODO("Not yet implemented")
//    }
//}