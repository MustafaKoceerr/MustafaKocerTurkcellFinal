package com.example.mustafakocer.presentation.shell

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.ActivityMainBinding
import com.example.mustafakocer.databinding.HeaderBinding
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.feature_auth.AuthActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The main "shell" activity of the application that hosts the primary navigation graph,
 * toolbar, and navigation drawer. It is responsible for observing app-wide state,
 * such as the current user's profile, from the [MainViewModel].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupNavigation()
        observeViewModel()
    }

    /**
     * Sets up the NavController, AppBarConfiguration, and connects the Toolbar and
     * NavigationView to the navigation graph.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.categoryFragment, R.id.searchFragment,
                R.id.ordersFragment, R.id.profileFragment, R.id.cartFragment
            ),
            drawerLayout = binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_logout) {
                viewModel.onLogoutClicked()
                binding.drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }

            val handled = menuItem.onNavDestinationSelected(navController)
            if (handled) {
                binding.drawerLayout.closeDrawers()
            }
            handled
        }
    }

    /**
     * Subscribes to the StateFlows and event channels from the [MainViewModel]
     * to update the UI and handle global events like logout.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe the user state to update the navigation drawer header.
                launch {
                    viewModel.userState.collect { resource ->
                        when (resource) {
                            is Resource.Success -> updateNavHeader(resource.data)
                            is Resource.Error -> {
                                // Değişiklik burada yapıldı: Toast -> Snackbar
                                Snackbar.make(binding.root, R.string.toast_user_info_error, Snackbar.LENGTH_SHORT).show()
                            }
                            else -> { /* No-op for Loading/Idle */ }
                        }
                    }
                }

                // Observe the logout event to navigate back to the authentication flow.
                launch {
                    viewModel.logoutEvent.collect {
                        goToAuthActivity()
                    }
                }
            }
        }
    }

    /**
     * Updates the content of the NavigationView's header with the user's information.
     * It uses ViewBinding for type-safe access to the header's views.
     */
    private fun updateNavHeader(user: User) {
        val headerView: View = binding.navView.getHeaderView(0)
        val headerBinding = HeaderBinding.bind(headerView)

        headerBinding.apply {
            txtNameHeader.text = user.fullName
            txtMailHeader.text = user.email
            Glide.with(this@MainActivity).load(user.imageUrl).into(imgViewHeader)
        }
    }

    /**
     * Navigates to the [AuthActivity] and clears the back stack, effectively
     * ending the current user session from a UI perspective.
     */
    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    /**
     * Handles the "Up" button navigation, delegating to the NavController.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}