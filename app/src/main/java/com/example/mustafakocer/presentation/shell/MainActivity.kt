package com.example.mustafakocer.presentation.shell

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Drawer kapandıktan sonra gideceğimiz menü id’sini burada tutacağız
    private var pendingTopLevelDestination: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupEdgeToEdge()
        setupNavigation()
        observeViewModel()
    }

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Not: Bu renk atamaları temanızda zaten var, burada olmaları zararsız ama zorunlu değil.
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val isDark =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }

        // --- DEĞİŞİKLİK BAŞLANGICI ---
        // AppBarLayout'un arka planını şeffaf yap ve gölgesini kaldır.
        // Sorunun ana çözümü bu iki satırdır.
        binding.appBar.setBackgroundColor(Color.TRANSPARENT)
        binding.appBar.elevation = 0f
        // --- DEĞİŞİKLİK SONU ---

        binding.drawerLayout.fitsSystemWindows = false
        binding.drawerLayout.setStatusBarBackground(null)
        binding.navView.fitsSystemWindows = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainerView) { v, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.updatePadding(bottom = bottom)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.navView) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = sys.top, bottom = sys.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.categoryFragment, R.id.searchFragment,
                R.id.ordersFragment, R.id.profileFragment, R.id.cartFragment
            ),
            drawerLayout = binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        // Bunu bırak: destination değişince NavigationView seçim durumunu otomatik günceller
        binding.navView.setupWithNavController(navController)

        // Drawer kapanınca bekleyen navigasyonu çalıştır
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                pendingTopLevelDestination?.let { dest ->
                    navigateWithCrossfade(dest)
                    pendingTopLevelDestination = null
                }
            }
        })

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_logout) {
                viewModel.onLogoutClicked()
                binding.drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }

            val currentDestId = navController.currentDestination?.id
            if (currentDestId == menuItem.itemId) {
                binding.drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }

            // Animasyonu görebilmek için navigate’i drawer kapanınca yap
            pendingTopLevelDestination = menuItem.itemId
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userState.collect { resource ->
                        when (resource) {
                            is Resource.Success -> updateNavHeader(resource.data)
                            is Resource.Error ->
                                Snackbar.make(
                                    binding.root,
                                    R.string.toast_user_info_error,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.logoutEvent.collect { goToAuthActivity() }
                }
            }
        }
    }

    private fun updateNavHeader(user: User) {
        val headerView: View = binding.navView.getHeaderView(0)
        val headerBinding = HeaderBinding.bind(headerView)
        headerBinding.apply {
            txtNameHeader.text = user.fullName
            txtMailHeader.text = user.email
            Glide.with(this@MainActivity).load(user.imageUrl).into(imgViewHeader)
        }
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun navigateWithCrossfade(destId: Int) {
        val options = navOptions {
            anim {
                enter = R.anim.fade_in
                exit = R.anim.fade_out
                popEnter = R.anim.fade_in
                popExit = R.anim.fade_out
            }
            // stack şişmesin: her seçimde Home (start) hariç üsttekileri temizle
            // (inclusive=false => Home kalır, geri tuşu Home’a döner)
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
            // NOTE: restore/saveState kullanmıyoruz; animasyonu engelleyebiliyor
        }
        try {
            navController.navigate(destId, null, options)
        } catch (_: IllegalArgumentException) {
            // ignore
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
