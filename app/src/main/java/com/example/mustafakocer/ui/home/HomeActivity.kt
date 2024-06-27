package com.example.mustafakocer.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.ActivityHomeBinding
import com.example.mustafakocer.ui.home.viewmodel.HomeActivityViewModel
import com.example.mustafakocer.ui.login.LoginActivity
import com.example.mustafakocer.util.UserId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navOptions: NavOptions
    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setContentView(binding.root)
        viewModel.getUser()

        observeUser()


        val toolbar = binding.toolbar as Toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar) // Now 'toolbar' is smart cast to Toolbar
        }

        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.searchFragment,
                R.id.favoriteFragment,
                R.id.orderFragment,
                R.id.profileFragment,
                R.id.cartFragment
            ), drawerLayout = binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = when (destination.id) {
                R.id.homeFragment -> "Home"
                R.id.categoryFragment -> "Categories"
                R.id.productsByCategoryFragment -> "Products by Categories"
                R.id.searchFragment -> "Search"
                R.id.favoriteFragment -> "Favorites"
                R.id.orderFragment -> "Orders"
                R.id.profileFragment -> "Profile"
                R.id.cartFragment -> "Cart"
                else -> "Home" // Varsayılan başlık
            }

        }

        binding.navView.setNavigationItemSelectedListener { it: MenuItem ->
            return@setNavigationItemSelectedListener navigationItemSelected(it)
        }

        binding.navView.bringToFront()
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            binding.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    private fun navigationItemSelected(it: MenuItem): Boolean {
        when (it.itemId) {
            R.id.nav_home -> {
                switchFragment(R.id.homeFragment)
            }

            R.id.nav_category -> {
                switchFragment(R.id.categoryFragment)
            }

            R.id.nav_search -> {
                switchFragment(R.id.searchFragment)
            }

            R.id.nav_favorite -> {
                switchFragment(R.id.favoriteFragment)
            }

            R.id.nav_order -> {
                switchFragment(R.id.orderFragment)
            }

            R.id.nav_profile -> {
                switchFragment(R.id.profileFragment)
            }

            R.id.nav_cart -> {
                switchFragment(R.id.cartFragment)
            }

            R.id.nav_logout -> {
                clearUserData()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            else -> {

                return false
            }
        }
        binding.drawerLayout.closeDrawers()

        return true
    }

    private fun switchFragment(fragId: Int) {
        navOptions = NavOptions.Builder()
            .setPopUpTo(
                fragId,
                true
            ) // nav_graph, navigation graph'inizin başlangıç destination'ıdır
            .build()

        navController.navigate(fragId, null, navOptions = navOptions)
    }


    private fun observeUser() {
        this.lifecycleScope.launch {

            viewModel.userInfo.collect { resource ->

                resource?.let {

                    val headerView: View = binding.navView.getHeaderView(0)
                    val headerName = headerView.findViewById<TextView>(R.id.txtNameHeader)
                    val headerMail = headerView.findViewById<TextView>(R.id.txtMailHeader)
                    val headerImage = headerView.findViewById<ImageView>(R.id.imgViewHeader)

                    it.image?.let { imageUrl ->
                        Glide.with(this@HomeActivity)
                            .load(imageUrl)
                            .into(headerImage)
                    }

                    it.email?.let { email ->
                        headerMail.text = email
                        val fullName = it.firstName + it.lastName
                        headerName.text = fullName
                    }

                    it.id?.let { id ->
                        UserId.userId = id.toInt()
                    }

                }
            }
        }
    }

    private fun clearUserData() {
        this.lifecycleScope.launch {

            viewModel.deleteUser()
            viewModel.clearDataStore()

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}