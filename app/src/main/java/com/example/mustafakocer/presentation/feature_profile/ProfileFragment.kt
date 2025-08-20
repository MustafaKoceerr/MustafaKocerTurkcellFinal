package com.example.mustafakocer.presentation.feature_profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentProfileBinding
import com.example.mustafakocer.domain.model.User
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
        // Retry butonu artık StateLayout tarafından yönetiliyor.
        binding.stateLayout.onRetry = { viewModel.fetchUserProfile() }

        // Güncelle butonu
        val updateButton = binding.contentView.findViewById<MaterialButton>(R.id.btnUpdateProfile)
        updateButton.setOnClickListener {
            val firstNameEt =
                binding.contentView.findViewById<TextInputEditText>(R.id.editFirstName)
            val lastNameEt = binding.contentView.findViewById<TextInputEditText>(R.id.editLastName)
            val emailEt = binding.contentView.findViewById<TextInputEditText>(R.id.editEmail)
            val phoneEt = binding.contentView.findViewById<TextInputEditText>(R.id.editPhone)
            val ageEt = binding.contentView.findViewById<TextInputEditText>(R.id.editAge)

            viewModel.updateProfile(
                firstName = firstNameEt.text?.toString().orEmpty(),
                lastName = lastNameEt.text?.toString().orEmpty(),
                email = emailEt.text?.toString().orEmpty(),
                phone = phoneEt.text?.toString().orEmpty(),
                age = ageEt.text?.toString().orEmpty()
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1) Tam ekran durum: Loading / Error / Content
                launch {
                    combine(viewModel.isLoading, viewModel.error) { isLoading, error ->
                        isLoading to error
                    }.collectLatest { (isLoading, error) ->
                        when {
                            isLoading -> binding.stateLayout.showLoading()
                            error != null -> {
                                binding.stateLayout.showError(subtitle = error.message)
                                viewModel.errorHandled()
                            }

                            else -> binding.stateLayout.showContent()
                        }
                    }
                }

                // 2) Kullanıcı verisi
                launch {
                    viewModel.user.collect { user ->
                        user?.let { populateUi(it) }
                    }
                }

                // 3) Güncelle butonu (tam ekran state değil)
                launch {
                    viewModel.isUpdating.collect { isUpdating ->
                        val updateBtn =
                            binding.contentView.findViewById<MaterialButton>(R.id.btnUpdateProfile)
                        updateBtn.isEnabled = !isUpdating
                        updateBtn.text = if (isUpdating) {
                            getString(R.string.profile_updating_button)
                        } else {
                            getString(R.string.profile_update_button)
                        }
                    }
                }

                // 4) Tek seferlik toast mesajları
                launch {
                    viewModel.toastMessage.collect { uiText ->
                        Toast.makeText(
                            requireContext(),
                            uiText.asString(requireContext()),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // Eski populateUi korunur
    private fun populateUi(user: User) {
        binding.apply {
            if (txtFullName.text.toString() != user.fullName) {
                txtFullName.text = user.fullName
            }
            txtCurrentUsername.text = getString(R.string.profile_username_format, user.username)

            Glide.with(requireContext())
                .load(user.imageUrl)
                .placeholder(R.drawable.ic_person_24)
                .into(imgProfile)

            if (editFirstName.text.toString() != user.firstName) editFirstName.setText(user.firstName)
            if (editLastName.text.toString() != user.lastName) editLastName.setText(user.lastName)
            if (editEmail.text.toString() != user.email) editEmail.setText(user.email)
            if (editPhone.text.toString() != user.phone) editPhone.setText(user.phone)
            if (editAge.text.toString() != user.age.toString()) editAge.setText(user.age.toString())
        }
    }
}
