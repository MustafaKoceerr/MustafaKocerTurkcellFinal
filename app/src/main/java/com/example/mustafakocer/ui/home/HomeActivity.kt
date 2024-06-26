package com.example.mustafakocer.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.example.mustafakocer.util.showToast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navOptions: NavOptions

    private lateinit var binding : ActivityHomeBinding


    private val viewModel: HomeActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setContentView(binding.root)


        observeUser()
        viewModel.getUser()

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
                R.id.cartFragment
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

        toolbar.setNavigationIcon(R.drawable.ic_shopping_cart)


    }

    private fun navigationItemSelected(it: MenuItem):Boolean{
        when(it.itemId){
            R.id.nav_home ->{this.showToast("home")
                switchFragment(R.id.homeFragment)
            }
            R.id.nav_category ->{
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
            R.id.nav_cart ->{
                switchFragment(R.id.cartFragment)
            }
            R.id.nav_logout ->{
                // todo logout islemini sagla
                clearUserData()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
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



    private fun observeUser() {
        this.lifecycleScope.launch {
            /*
            When collecting from a StateFlow in your UI, you should typically use Dispatchers.Main.
            This is because you usually want to update the UI in response to the collected state,
            and all UI updates must occur on the main thread.
             */
            viewModel.userInfo.collect { resource ->
                resource?.let {
                    Log.d("getuser","Buraya girdi ve resource $it")
                    val headerView :View = binding.navView
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

                        // İsim kontrolünü ve atamasını burada yapalım
                        val fullName = it.firstName + it.lastName
                        headerName.text = fullName
                    }

                    it.id?.let { id ->
                        UserId.userId = id.toInt()
                    }

                }
                if (resource==null)
                    Log.d("isdeleted","viewmodel getuser cagirildi ve user basariyla silindi")

            }

        }
    }

    private fun clearUserData(){
        this.lifecycleScope.launch {
            val isDeleted = viewModel.deleteUser()
            Log.d("isdeleted","silindi mi : $isDeleted")
            val value = viewModel.getAuthToken().first()
                // Burada value, Flow'un içindeki değeri temsil eder
                if (value != null) {
                    Log.d("isdeleted", "dataStore'dan silmeden önce değeri $value")
                } else{
                    Log.d("isdeleted", "dataStore'dan silindi")
                }


            Log.d("isdeleted", "before clearDataStore")

            viewModel.clearDataStore()
            Log.d("isdeleted", "after clearDataStore")

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            Log.d("isdeleted", "intent işlemi")
        }
        }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy işlemi gerçkelşetir")

    }




}