package com.example.mustafakocer.ui.home

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.ActivityHomeBinding
import com.example.mustafakocer.util.showToast

class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navOptions: NavOptions

    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setContentView(binding.root)

        // enableEdgeToEdge()
        //setContentView(R.layout.activity_home)

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
            ), drawerLayout = binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { it: MenuItem ->
            return@setNavigationItemSelectedListener navigationItemSelected(it)
        }

        binding.navView.bringToFront()
        val toggle : ActionBarDrawerToggle = ActionBarDrawerToggle(this@HomeActivity,binding.drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



    }

    private fun navigationItemSelected(it: MenuItem):Boolean{
        when(it.itemId){
            R.id.nav_home ->{this.showToast("home")
                switchFragment(R.id.homeFragment)
            }
            R.id.nav_category ->{this.showToast("nav_category")
                switchFragment(R.id.categoryFragment)

            }
            R.id.nav_search ->{
                switchFragment(R.id.searchFragment)

            }
            R.id.nav_favorite ->{
                switchFragment(R.id.favoriteFragment)

            }
            R.id.nav_order ->{
                switchFragment(R.id.orderFragment)

            }
            R.id.nav_profile ->{
                switchFragment(R.id.profileFragment)

            }

            R.id.nav_logout ->{
                // todo logout islemini sagla
            }

            else -> {return  false}
        }
        binding.drawerLayout.closeDrawers()

        return true
    }

    private fun switchFragment(fragId: Int){
        navOptions = NavOptions.Builder()
            .setPopUpTo(fragId, true) // nav_graph, navigation graph'inizin başlangıç destination'ıdır
            .build()

        navController.navigate(fragId, null,navOptions = navOptions)
    }


}