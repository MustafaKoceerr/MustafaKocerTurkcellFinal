package com.example.mustafakocer.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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


        setSupportActionBar(binding.toolbar)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        // bottom bar'da veya drawer'da gözükecek olanları buraya koyuyoruz.
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

        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)


        // Kendi listenerımı eklemişim, bu eklendiği yerde de 1 kere çalışır, sonrasında destination değiştiğinde de çalışır.
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
        /*
        // Böyle setNavigationItemSelectedListener ile özel işlemler de tanımlayabilirsin
        // Ben bu uygulamam için, navigationun default davranışını kullanıp, logout fragment'ını kullanıp
        // todo:logout fragment onCreate'inde çıkış işlemlerini yapacağım.
          binding.navView.setNavigationItemSelectedListener { menuItem: MenuItem ->
              when (menuItem.itemId) {
                  R.id.nav_logout -> {
                      return@setNavigationItemSelectedListener navigationItemSelected(menuItem)
                  }
                  else -> {
                      navController.navigate(menuItem.itemId)
                      // Diğer durumlarda varsayılan navController ile navigasyonu gerçekleştirin
                      // Örneğin, navController.navigate(menuItem.itemId) gibi bir işlem yapabilirsiniz
                      true // true döndürerek işlemin başarıyla gerçekleştiğini belirtin
                  }
              }
          }
         */

        binding.navView.bringToFront()
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    // Otomatik olarak toolbar'ının yönetilmesini istiyorsan, örneğin up butonu çıksın vs, bunu kullan.
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigationItemSelected(it: MenuItem): Boolean {
        when (it.itemId) {
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