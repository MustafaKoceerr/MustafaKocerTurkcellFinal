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
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        viewModel.fetchUserProfile()
    }
    // ------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModelStates()
    }

    private fun setupClickListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            viewModel.updateProfile(
                firstName = binding.editFirstName.text.toString(),
                lastName = binding.editLastName.text.toString(),
                email = binding.editEmail.text.toString(),
                phone = binding.editPhone.text.toString(),
                age = binding.editAge.text.toString()
            )
        }
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Ana UI durumunu (isLoading, error, user) tek bir yerden dinle
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        // Sadece ilk yükleme durumunda tam ekran yükleme göstergesini yönet.
                        if (!viewModel.isUpdating.value) {
                            binding.viewLoadingStub.isVisible = isLoading
                            binding.contentView.isVisible = !isLoading
                        }
                    }
                }

                launch {
                    viewModel.error.collectLatest { exception ->
                        val hasError = exception != null
                        binding.contentView.isVisible = !hasError
                        if (hasError) {
                            handleErrorState(binding.viewErrorStub, exception!!)
                            viewModel.errorHandled()
                        } else {
                            hideErrorState()
                        }
                    }
                }

                launch {
                    viewModel.user.collect { user ->
                        user?.let {
                            if (!viewModel.isLoading.value && viewModel.error.value == null) {
                                binding.contentView.isVisible = true
                                populateUi(it)
                            }
                        }
                    }
                }

                // 2. Ayrı durumları (isUpdating, toastMessage) dinlemeye devam et
                launch {
                    viewModel.isUpdating.collect { isUpdating ->
                        binding.btnUpdateProfile.isEnabled = !isUpdating
                        binding.btnUpdateProfile.text =
                            if (isUpdating) getString(R.string.profile_updating_button) else getString(R.string.profile_update_button)
                    }
                }

                launch {
                    viewModel.toastMessage.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun populateUi(user: User) {
        binding.apply {
            txtFullName.text = user.fullName
            txtCurrentUsername.text = getString(R.string.profile_username_format, user.username)
            Glide.with(requireContext())
                .load(user.imageUrl)
                .placeholder(R.drawable.ic_person_24)
                .into(imgProfile)

            editFirstName.setText(user.firstName)
            editLastName.setText(user.lastName)
            editEmail.setText(user.email)
            editPhone.setText(user.phone)
            editAge.setText(user.age.toString())
        }
    }
}