package stoyck.vitrina.domain

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import stoyck.vitrina.R
import stoyck.vitrina.domain.preferences.PreferencesData
import stoyck.vitrina.domain.usecase.*
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.ui.suggestion.SubredditSuggestionData
import stoyck.vitrina.util.Debouncer
import java.net.UnknownHostException
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
    private val context: Context,
    // Use case names added to the end, because
    private val getSubredditHintsUseCase: GetSubredditHintsUseCase,
    private val tryAddSubredditUseCase: TryAddSubredditUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val loadSubredditsUseCase: LoadSubredditsUseCase,
    private val saveSubredditsUseCase: SaveSubredditsUseCase
) {

    private val job = Dispatchers.IO + SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // No special error handling for this one,
        // as it is just about setting the message
        GlobalScope.launch {
            showMessageAndLog(throwable)
        }
    }

    private val scope = CoroutineScope(job + exceptionHandler)

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

    private val _subreddits: MutableLiveData<List<PersistedSubredditData>> = MutableLiveData(
        emptyList()
    )

    val subreddits: LiveData<List<PersistedSubredditData>> = _subreddits

    private val _subredditSuggestions: MutableLiveData<List<SubredditSuggestionData>> =
        MutableLiveData(
            ArrayList()
        )

    val subredditSuggestions: LiveData<List<SubredditSuggestionData>> = _subredditSuggestions

    // Can be set from anywhere
    private val _userMessage: MutableLiveData<String?> = MutableLiveData(null)

    val userMessage: LiveData<String?> = _userMessage

    // So that when the user returns to the app,
    // the last toast is not shown
    private val userMessageClearupDebouncer = Debouncer(delayMillis = 100L)

    fun setUserMessageAsync(message: String?) {
        scope.launch {
            setUserMessage(message)
        }
    }

    private suspend fun setUserMessage(message: String?) {
        withContext(Dispatchers.Main) {
            _userMessage.value = message

            if (message != null) {
                userMessageClearupDebouncer {
                    _userMessage.value = null
                }
            }
        }
    }

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

    private suspend fun showMessageAndLog(exception: Throwable): Boolean {
        when (exception) {
            is UserReadableException -> {
                setUserMessage(exception.userReadableMessage)
            }
            is UnknownHostException -> {
                val noInternet = context.getString(R.string.error_no_internet)
                setUserMessage(noInternet)
            }
            else -> {
                val noIdeaError = context.getString(R.string.error_no_idea)
                setUserMessage(noIdeaError)

                FirebaseCrashlytics.getInstance()
                    .recordException(exception)
            }
        }

        return true
    }

    init {
        loadSettings()
        loadSubreddits()
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

    private fun loadSubreddits() {
        scope.launch {
            val data = loadSubredditsUseCase()

            withContext(Dispatchers.Main) {
                this@MainViewModel._subreddits.value = data
            }
        }
    }

    fun saveSubreddits(subreddits: List<PersistedSubredditData>) {
        scope.launch {
            saveSubredditsUseCase(subreddits)

            withContext(Dispatchers.Main) {
                this@MainViewModel._subreddits.value = subreddits
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
            val progressMessage = context.getString(R.string.message_subreddit_being_added)
            setUserMessage(progressMessage.format("/r/$subreddit"))

            withLoading {
                val result = tryAddSubredditUseCase(
                    requestedSubredditName = subreddit
                )

                val exception = result.exceptionOrNull()

                if (exception != null) {
                    showMessageAndLog(exception)
                    return@withLoading
                }

                val (addedSubreddit, subreddits) = result.getOrNull()
                    ?: throw RuntimeException("Should not be null if success - failure handled earlier")

                withContext(Dispatchers.Main) {
                    _subreddits.value = subreddits

                    val successMessage =
                        context.getString(R.string.message_subreddit_successfully_added)
                    setUserMessage(successMessage.format("/r/${addedSubreddit.name}"))
                }
            }
        }
    }

}