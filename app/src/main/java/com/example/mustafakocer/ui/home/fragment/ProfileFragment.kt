package com.example.mustafakocer.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentProfileBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.viewmodel.ProfileViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var remoteConfig: FirebaseRemoteConfig
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


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

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater,container,false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getAuthToken(){
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
                        Toast.makeText(requireContext(), "User Geldi${resource.value}", Toast.LENGTH_SHORT).show()
                        Log.d("flow","${resource.value}")
                        // Use the products data to update the UI
                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Hata ${resource.errorCode}  ${resource.errorBody}", Toast.LENGTH_SHORT).show()
                        Log.d("Hata","${resource.errorCode}  ${resource.errorBody}")

                    }
                    is Resource.Waiting ->{
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

    }


}