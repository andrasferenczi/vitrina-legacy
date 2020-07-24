package stoyck.vitrina.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import stoyck.vitrina.domain.preferences.PreferencesData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom stuff that helps the MainActivity with updating it's values
 *
 * Using architecture components is too complicated with Dagger 2
 * It's probably fine with Application-scope coroutines instead Activity-scoped
 */
@Singleton
class MainViewModel @Inject constructor(

) {
    enum class MenuState {
        /**
         * Actions where search and settings can be triggered
         */
        Default,

        /**
         * User is performing search
         */
        Search;
    }

    //

    private val _preferencesState: MutableLiveData<PreferencesData> =
        MutableLiveData(PreferencesData.DEFAULT)

    val preferencesState: LiveData<PreferencesData> = _preferencesState

    private val _menuState: MutableLiveData<MenuState> = MutableLiveData(MenuState.Default)

    val menuState: LiveData<MenuState> = _menuState

    //

    fun toDefaultMenu() {
        _menuState.value = MenuState.Default
    }

    fun toSearchMenu() {
        _menuState.value = MenuState.Search
    }

    fun updatePreferences(data: PreferencesData) {
        // todo: save
        this._preferencesState.value = data
    }

    fun updateSuggestionList(text: String) {

    }

    fun tryAddSubreddit(text: String) {

    }

}