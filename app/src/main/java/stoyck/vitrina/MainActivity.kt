package stoyck.vitrina

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

    enum class MenuState {
        /**
         * Actions where search and settings can be triggered
         */
        Default,

        /**
         * User is performing search
         */
        Search;

        fun next(): MenuState =
            when (this) {
                Default -> Search
                Search -> Default
            }

    }

    private var menuState: MenuState = MenuState.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar_default)

        setupDefaultToolbar()

        // https://stackoverflow.com/a/39474030/4420543
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)

//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            toolbar,
//            R.string.nav_app_bar_open_drawer_description,
//            R.string.nav_header_desc
//        )
//
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

//        toggle.isDrawerIndicatorEnabled = true
        // Custom icon, like settings
//        toolbar.setNavigationIcon(R.drawable.ic_settings)

//        drawerLayout.openDrawer(GravityCompat.START, true)

//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    private fun toSearchState() {
        this.menuState = MenuState.Search
        invalidateOptionsMenu()

        setupSearchToolbar()
    }

    private fun toDefaultState() {
        this.menuState = MenuState.Default
        invalidateOptionsMenu()

        setupDefaultToolbar()
    }

    private fun setupDefaultToolbar() {
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

    private fun setupSearchToolbar() {
        defaultToolbar.visibility = View.GONE
        searchToolbar.let {
            it.visibility = View.VISIBLE

            setSupportActionBar(searchToolbar)
            it.setNavigationIcon(R.drawable.ic_keyboard_backspace)
            it.setNavigationOnClickListener {
                toDefaultState()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (this.menuState) {
            MenuState.Default -> {
                menuInflater.inflate(R.menu.main_default, menu)
                val search = menu.findItem(R.id.action_search)

                search.setOnMenuItemClickListener {
                    toSearchState()
                    true
                }
            }
            MenuState.Search -> {
                menuInflater.inflate(R.menu.main_search, menu)

                val addFromText = menu.findItem(R.id.action_add_from_text)
                addFromText.setOnMenuItemClickListener {
                    true
                }
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (this.menuState == MenuState.Search) {
            this.toDefaultState()
            return
        }

        super.onBackPressed()
    }
}