package com.example.mustafakocer.presentation.feature_profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentProfileBinding
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            // EditText'lerden güncel verileri al
            val firstName = binding.inputLayoutFirstName.editText?.text.toString()
            val lastName = binding.inputLayoutLastName.editText?.text.toString()
            val email = binding.inputLayoutEmail.editText?.text.toString()
            val phone = binding.inputLayoutPhone.editText?.text.toString()
            val age = binding.inputLayoutAge.editText?.text.toString()

            // ViewModel'deki public fonksiyonu çağır
            viewModel.updateProfile(firstName, lastName, email, phone, age)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle, Fragment'ın view'ı STARTED durumundayken çalışır,
            // STOPPED olduğunda coroutine'i durdurur. Bu, memory leak'leri önler.
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Kullanıcı verisini dinle
                launch {
                    viewModel.user.collect { user ->
                        user?.let { populateUi(it) }
                    }
                }

                // 2. İlk yüklenme durumunu dinle
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // Sadece ilk yüklenme durumunda ana progressbar'ı göster/gizle
                        if (!viewModel.isUpdating.value) {
                            binding.progressbar.isVisible = isLoading
                        }
                    }
                }

                // 3. güncelleme durumunu dinle.
                launch {
                    viewModel.isUpdating.collect { isUpdating ->
                        // Güncelleme sırasında butonu devre dışı bırak ve progressbar'ı göster
                        binding.btnUpdateProfile.isEnabled = !isUpdating
                        binding.progressbar.isVisible = isUpdating
                        binding.btnUpdateProfile.text =
                            if (isUpdating) "Güncelleniyor..." else "Bilgileri Güncelle"
                    }
                }

                // 4. Toast mesajlarını dinle
                launch {
                    viewModel.toastMessage.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    /**
     * Gelen User nesnesi ile UI bileşenlerini doldurur.
     */
    private fun populateUi(user: User) {
        binding.apply {
            // Başlık kısmını doldur
            txtFullName.text = user.fullName
            txtCurrentUsername.text = "@${user.username}"
            Glide.with(requireContext())
                .load(user.imageUrl)
                .placeholder(R.drawable.ic_person_24) // Yüklenirken gösterilecek varsayılan ikon
                .into(imgProfile)

            // EditText alanlarını doldur
            editFirstName.setText(user.firstName)
            editLastName.setText(user.lastName)
            editEmail.setText(user.email)
            editPhone.setText(user.phone)
            editAge.setText(user.age.toString())
        }
    }
}