package com.example.mustafakocer.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentProfileBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.viewmodel.ProfileViewModel
import com.example.mustafakocer.util.visibleProgressBar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAndApplyBackgroundColor()
        binding.progressbar.visibility = View.GONE

        getAuthToken()
        // todo burada user'ın id'sini localdb'den veya token ile istek atarak çek, sonrasında viewModel'e parametre ata
    }

    private fun fetchAndApplyBackgroundColor() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val backgroundColor = remoteConfig.getString("background_color")
                    applyBackgroundColor(backgroundColor)
                } else {
                    // Hata durumunda varsayılan rengi kullanabilirsiniz
                    val defaultColor = "#FFFFFF"
                    applyBackgroundColor(defaultColor)
                }
            }
    }

    private fun applyBackgroundColor(colorString: String) {
        val color = Color.parseColor(colorString)
        binding.profileFragmentRoot.setBackgroundColor(color)
    }


    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }


    private fun getAuthToken() {
        viewLifecycleOwner.lifecycleScope.launch {

            val token = viewModel.getAuthToken().first()
            token?.let {
                viewModel.getUser(token)
            }
            observeUser()

        }
    }

    private suspend fun observeUser() {
        viewModel.user.collect { resource ->
            binding.progressbar.visibleProgressBar(false)

            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator
                    binding.progressbar.visibleProgressBar(true)
                }

                is Resource.Success -> {
                    // Update UI with products data
                    Toast.makeText(
                        requireContext(),
                        "User Geldi${resource.value}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("flow", "${resource.value}")
                    // Use the products data to update the UI
                }

                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Hata ${resource.errorCode}  ${resource.errorBody}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("Hata", "${resource.errorCode}  ${resource.errorBody}")

                }

                is Resource.Waiting -> {
                    binding.progressbar.visibleProgressBar(false)
                }
            }

        }

    }


}