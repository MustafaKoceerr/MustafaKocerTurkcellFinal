package com.example.mustafakocer.presentation.feature_profile

import android.os.Bundle
import android.view.View
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Displays the user's profile and allows for editing and updating the information.
 * This fragment observes multiple StateFlows from the [ProfileViewModel] to manage
 * its complex UI, which includes a full-screen state layout and partial state updates
 * for the content.
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    /**
     * Sets up click listeners for the interactive elements on the screen.
     */
    private fun setupClickListeners() {
        binding.stateLayout.onRetry = { viewModel.fetchUserProfile() }

        val updateButton = binding.contentView.findViewById<MaterialButton>(R.id.btnUpdateProfile)
        updateButton.setOnClickListener {
            val firstNameEt = binding.contentView.findViewById<TextInputEditText>(R.id.editFirstName)
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

    /**
     * Subscribes to all StateFlows and SharedFlows from the [ProfileViewModel]
     * to update the UI in a lifecycle-aware manner.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe the main screen state (loading/error)
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

                // Observe the user data to populate the fields
                launch {
                    viewModel.user.collect { user ->
                        user?.let { populateUi(it) }
                    }
                }

                // Observe the update button's loading state
                launch {
                    viewModel.isUpdating.collect { isUpdating ->
                        val updateBtn = binding.contentView.findViewById<MaterialButton>(R.id.btnUpdateProfile)
                        updateBtn.isEnabled = !isUpdating
                        updateBtn.text = if (isUpdating) {
                            getString(R.string.profile_updating_button)
                        } else {
                            getString(R.string.profile_update_button)
                        }
                    }
                }

                // Observe one-time snackbar messages
                launch {
                    viewModel.snackbarMessage.collect { uiText ->
                        Snackbar.make(binding.root, uiText.asString(requireContext()), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /**
     * Populates the UI fields with the user's data.
     * It includes checks to prevent resetting the cursor position in EditTexts
     * if the text has not changed.
     */
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