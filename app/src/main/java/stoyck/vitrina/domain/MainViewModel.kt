package stoyck.vitrina.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.*
import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.domain.usecase.GetSubredditHintsUseCase
import stoyck.vitrina.domain.usecase.LoadSettingsUseCase
import stoyck.vitrina.domain.usecase.SaveSettingsUseCase
import stoyck.vitrina.domain.usecase.TryAddSubredditUseCase
import stoyck.vitrina.ui.SubredditSuggestionData
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
    // Use case names added to the end, because
    private val getSubredditHintsUseCase: GetSubredditHintsUseCase,
    private val tryAddSubredditUseCase: TryAddSubredditUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) {

    private val job = Dispatchers.IO + SupervisorJob()

    private val scope = CoroutineScope(job)

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

    private val _subredditSuggestions: MutableLiveData<List<SubredditSuggestionData>> =
        MutableLiveData(
            ArrayList()
        )

    val subredditSuggestions: LiveData<List<SubredditSuggestionData>> = _subredditSuggestions

    private val _loadingCount = MutableLiveData(0)

    val isLoading: LiveData<Boolean> = _loadingCount.map { it!! > 0 }

    private suspend fun withLoading(
        block: suspend () -> Unit
    ) {
        try {
            withContext(Dispatchers.Main) {
                _loadingCount.value = _loadingCount.value!! + 1
            }

            block()
        } catch (e: Exception) {
            throw e
        } finally {
            withContext(Dispatchers.Main) {
                _loadingCount.value = _loadingCount.value!! - 1
            }
        }
    }

    init {
        loadSettings()
    }

    //

    fun toDefaultMenu() {
        _menuState.value = MenuState.Default
    }

    fun toSearchMenu() {
        _menuState.value = MenuState.Search
    }

    private fun loadSettings() {
        scope.launch {
            val data = loadSettingsUseCase()

            withContext(Dispatchers.Main) {
                _preferencesState.value = data
            }
        }
    }

    fun updatePreferences(data: PreferencesData) {
        scope.launch {
            saveSettingsUseCase(data)

            withContext(Dispatchers.Main) {
                _preferencesState.value = data
            }
        }
    }

    fun updateSuggestionList(text: String) {
        if (text.isBlank()) {
            _subredditSuggestions.value = emptyList()
            return
        }

        scope.launch {
            withLoading {
                val suggestions = getSubredditHintsUseCase(
                    partialSubredditName = text
                )

                withContext(Dispatchers.Main) {
                    _subredditSuggestions.value = suggestions
                }
            }
        }
    }

    fun tryAddSubreddit(subreddit: String) {
        scope.launch {
//            val result = tryAddSubredditUseCase(
//                requestedSubredditName = subreddit
//            )
//
//            withContext(Dispatchers.Main) {
//                _subredditSuggestions.value = suggestions
//            }
        }
    }

}