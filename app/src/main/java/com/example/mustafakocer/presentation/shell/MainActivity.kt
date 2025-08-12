package com.example.mustafakocer.presentation.shell

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected // Bu import doğru
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.ActivityMainBinding
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.feature_auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        observeUserState()
        observeLogoutEvent()
    }

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

            // DEĞİŞTİ: Fonksiyonu doğru şekilde, menuItem üzerinden çağırıyoruz.
            val handled = menuItem.onNavDestinationSelected(navController)
            if (handled) {
                binding.drawerLayout.closeDrawers()
            }
            handled
        }
    }

    private fun observeUserState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> { /* No-op */ }
                        is Resource.Success -> { updateNavHeader(resource.data) }
                        is Resource.Error -> {
                            Toast.makeText(this@MainActivity, "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Idle -> { /* No-op */ }
                    }
                }
            }
        }
    }

    private fun observeLogoutEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutEvent.collect {
                    goToAuthActivity()
                }
            }
        }
    }

    private fun updateNavHeader(user: User) {
        val headerView: View = binding.navView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.txtNameHeader)
        val headerMail = headerView.findViewById<TextView>(R.id.txtMailHeader)
        val headerImage = headerView.findViewById<ImageView>(R.id.imgViewHeader)

        headerName.text = user.fullName
        headerMail.text = user.email
        Glide.with(this).load(user.imageUrl).into(headerImage)
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}