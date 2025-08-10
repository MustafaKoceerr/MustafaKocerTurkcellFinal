package com.example.mustafakocer.presentation.shell

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    // YENİ EKLENDİ: Toolbar'daki badge TextView'ine referans tutmak için.
    private lateinit var cartBadgeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupNavigation()

        // ViewModel'den gelen state ve event'leri dinle.
        observeUserState()
        observeLogoutEvent()
        observeCartCount() // YENİ FONKSİYON ÇAĞRISI: Sepet sayısını dinlemeye başla.
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

        // Bu satır, drawer'daki item'ların seçili durumunu (highlight) yönetir.
        binding.navView.setupWithNavController(navController)

        // Tıklama olaylarını manuel olarak yönetiyoruz.
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // 1. Önce bizim özel durumumuzu (logout) kontrol et.
            if (menuItem.itemId == R.id.nav_logout) {
                viewModel.onLogoutClicked()
                binding.drawerLayout.closeDrawers()
                // Olayı biz ele aldık, true döndürerek işlemi bitir.
                return@setNavigationItemSelectedListener true
            }

            // 2. DÜZELTME: Diğer tüm durumlar için, standart navigasyon davranışını tetikle.
            // Bu "sihirli" satır, tıklanan menü item'ının ID'si ile nav graph'taki
            // fragment ID'sini eşleştirir ve navigasyonu gerçekleştirir.
            val handled = androidx.navigation.ui.NavigationUI.onNavDestinationSelected(menuItem, navController)

            // 3. Eğer standart navigasyon başarılı olduysa, drawer'ı kapat.
            if (handled) {
                binding.drawerLayout.closeDrawers()
            }

            // 4. Sonucu döndür.
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

    // YENİ FONKSİYON: ViewModel'deki sepet sayısını dinler.
    private fun observeCartCount() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItemCount.collect { count ->
                    // Eğer menü oluşturulmuş ve cartBadgeTextView başlatılmışsa, badge'i güncelle.
                    // Bu kontrol, collect'in menü oluşturulmadan önce çalışması durumunda
                    // uygulamayı çökmekten kurtarır.
                    if (::cartBadgeTextView.isInitialized) {
                        updateCartBadge(count)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        val cartItem = menu.findItem(R.id.menu_cart)
        val actionView = cartItem?.actionView // 1. cartItem'ın null olma ihtimaline karşı safe call (?.)

        // 2. actionView'ın null olmadığını kontrol et
        actionView?.let { view ->
            // Bu blok sadece actionView null değilse çalışır.
            // 'view' artık non-nullable (null olamayan) bir View'dır.

            // Badge TextView'ine referansı al
            cartBadgeTextView = view.findViewById(R.id.cart_badge)

            // Sepet ikonuna tıklandığında CartFragment'a git
            view.setOnClickListener {
                onOptionsItemSelected(cartItem)
            }
        }

        // 3. ViewModel'deki mevcut sayıyı hemen yansıt (güvenlik kontrolü ile)
        // Eğer bu kod menü oluşturulmadan önce çalışırsa çökmemesi için.
        if (::cartBadgeTextView.isInitialized) {
            updateCartBadge(viewModel.cartItemCount.value)
        }

        return true
    }

    // YENİ FONKSİYON: Menüdeki item'lara tıklandığında ne olacağını yönetir.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Menüdeki sepet ikonuna tıklandığında CartFragment'a git
        return when (item.itemId) {
            R.id.menu_cart -> {
                navController.navigate(R.id.cartFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // YENİ FONKSİYON: Gelen sayıya göre badge'in görünürlüğünü ve metnini ayarlar.
    private fun updateCartBadge(count: Int) {
        if (count == 0) {
            cartBadgeTextView.visibility = View.GONE
        } else {
            cartBadgeTextView.visibility = View.VISIBLE
            cartBadgeTextView.text = count.toString()
        }
    }
}