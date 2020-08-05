package stoyck.vitrina

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_subreddit_suggestion.*
import stoyck.vitrina.domain.MainViewModel
import stoyck.vitrina.util.DebouncedTextWatcher
import stoyck.vitrina.util.hideKeyboard
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as VitrinaApplication).appComponent
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        viewModel.menuState.observe(this) {
            when (it) {
                MainViewModel.MenuState.Default -> renderDefaultState()
                MainViewModel.MenuState.Search -> renderSearchState()
            }
        }

        viewModel.subredditSuggestions.observe(this) {
            subredditSuggestionRecyclerView.setData(it)
        }

        subredditSuggestionRecyclerView.onSubredditSuggestionClicked = { subredditToAdd ->
            toDefaultMenu()
            viewModel.tryAddSubreddit(subredditToAdd.name)
        }

        setupSettings()

        viewModel.isLoading.observe(this) { loading ->
            mainProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.userMessage.observe(this) { message ->
            if (message != null) {
                showSnackbar(message)
            }
        }
    }

    private fun toDefaultMenu() {
        hideKeyboard()
        subredditInputText.setText("")
        viewModel.toDefaultMenu()
    }

    private fun setupSettings() {
        // Shuffle
        val shuffleSwitch = navigationView
            .menu
            .findItem(R.id.settingsShuffle)
            ?.actionView as? Switch ?: throw RuntimeException("shuffle not found")

        // Over 18
        val over18Switch = navigationView
            .menu
            .findItem(R.id.settingsOver18)
            ?.actionView as? Switch ?: throw RuntimeException("over18 not found")

        //

        shuffleSwitch.setOnCheckedChangeListener { _, isChecked ->
            val current = viewModel.preferencesState.value!!
            viewModel.updatePreferences(current.copy(shuffle = isChecked))
        }
        viewModel.preferencesState.observe(this) {
            over18Switch.isChecked = it.isOver18
        }

        over18Switch.setOnCheckedChangeListener { _, isChecked ->
            val current = viewModel.preferencesState.value!!
            viewModel.updatePreferences(current.copy(isOver18 = isChecked))
        }
        viewModel.preferencesState.observe(this) {
            shuffleSwitch.isChecked = it.shuffle
        }
    }

    private fun renderSearchState() {
        fun setupSearchToolbar() {
            defaultToolbar.visibility = View.GONE
            searchToolbar.let {
                it.visibility = View.VISIBLE

                setSupportActionBar(searchToolbar)
                it.setNavigationIcon(R.drawable.ic_keyboard_backspace)
                it.setNavigationOnClickListener {
                    toDefaultMenu()
                }

                subredditInputText.addTextChangedListener(
                    DebouncedTextWatcher(300L) { text ->
                        viewModel.updateSuggestionList(text)
                    })

                subredditInputText.setOnEditorActionListener { view, id, event ->
                    val text = view.text.toString()
                    toDefaultMenu()
                    viewModel.tryAddSubreddit(text)
                    true
                }
            }
        }

        //
        contentMainContainer.visibility = View.GONE
        contentSubredditSuggestionContainer.visibility = View.VISIBLE

        invalidateOptionsMenu()

        addSubredditFab.visibility = View.GONE

        setupSearchToolbar()
    }

    private fun renderDefaultState() {
        fun setupDefaultToolbar() {
            searchToolbar.visibility = View.GONE

            defaultToolbar.let {
                it.visibility = View.VISIBLE
                setSupportActionBar(it)

                val toggle = ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    it,
                    R.string.nav_app_bar_open_drawer_description,
                    R.string.nav_header_desc
                )

                /// not add, because this might be called multiple times
                drawerLayout.setDrawerListener(toggle)
                toggle.syncState()

                // AFTER syncState
                it.setNavigationIcon(R.drawable.ic_settings)
            }

            supportActionBar?.title = this.resources.getString(R.string.main_screen_title)
        }

        //
        contentMainContainer.visibility = View.VISIBLE
        contentSubredditSuggestionContainer.visibility = View.GONE

        invalidateOptionsMenu()

        addSubredditFab.visibility = View.VISIBLE
        addSubredditFab.setOnClickListener {
            viewModel.toSearchMenu()
        }

        setupDefaultToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        viewModel.menuState.value?.let {
            when (it) {
                MainViewModel.MenuState.Default -> {
                    menuInflater.inflate(R.menu.main_default, menu)
                    val search = menu.findItem(R.id.action_search)
                    search.setOnMenuItemClickListener {
                        viewModel.toSearchMenu()
                        true
                    }
                }
                MainViewModel.MenuState.Search -> {
                    menuInflater.inflate(R.menu.main_search, menu)
                    val actionClearInputText = menu.findItem(R.id.action_clear_input_text)
                    actionClearInputText.setOnMenuItemClickListener {
                        if (subredditInputText.getText()?.toString()?.trim() == "") {
                            toDefaultMenu()
                        } else {
                            subredditInputText.setText("")
                            viewModel.updateSuggestionList("")
                        }

                        true
                    }
                }
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (viewModel.menuState.value == MainViewModel.MenuState.Search) {
            toDefaultMenu()
            return
        }

        super.onBackPressed()
    }


    fun showSnackbar(message: String) {
        Snackbar.make(contentMainContainer, message, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }
}