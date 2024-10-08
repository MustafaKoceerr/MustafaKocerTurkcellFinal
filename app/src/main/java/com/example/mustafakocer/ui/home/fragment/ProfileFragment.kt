package com.example.mustafakocer.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.databinding.FragmentProfileBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.viewmodel.ProfileViewModel
import com.example.mustafakocer.util.UserId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        observeNewData()

        binding.btnUpdateUser.setOnClickListener {
            var name: String? = binding.editFirstName.text.toString()
            var lastName: String? = binding.editLastName.text.toString()
            var age: String? = binding.editAge.text.toString()
            var gender: String? = binding.editGender.text.toString()
            var email: String? = binding.editEmail.text.toString()
            var phone: String? = binding.editPhone.text.toString()
            var username: String? = binding.editUsername.text.toString()

            if (name == "") {
                name = null
            }
            if (lastName == "") {
                lastName = null
            }
            if (age == "") {
                age = null
            }
            if (gender == "") {
                gender = null
            }
            if (email == "") {
                email = null
            }
            if (phone == "") {
                phone = null
            }
            if (username == "") {
                username = null
            }


            var tempUser = User(
                firstName = name,
                lastName = lastName,
                age = age?.toInt(),
                gender = gender,
                email = email,
                phone = phone,
                username = username
            )

            Log.d("tempuser", "gönderilen user $tempUser")
            viewModel.UpdateUser(UserId.userId, tempUser)
        }

        // todo istek gidiyor, cevap geliyor ama verileri UI'A taşıyamıyorum.
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


    private fun observeNewData() {

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.updateUser.collect { value ->
                when(value){
                    is Resource.Failure -> Log.d("tempuser", "Flow failure")
                    is Resource.Loading -> Log.d("tempuser", "Flow loading")
                    is Resource.Success -> {
                        Log.d("tempuser", "Flow success")
                        Log.d("tempuser", "${value.value}")
                        updateViewUser(
                            value.value.firstName,
                            value.value.lastName,
                            value.value.age,
                            value.value.gender,
                            value.value.email,
                            value.value.phone,
                            value.value.username,
                            )

                    }
                    is Resource.Waiting -> Log.d("tempuser", "Flow waiting")
                }
            }

        }
    }

    private fun updateViewUser(
        name: String?,
        lastName: String?,
        age: Int?,
        gender: String?,
        email: String?,
        phone: String?,
        username: String?
    ) {
        Log.d("tempuser", "tempuser name $name")

        binding.txtFirstName.setText(name)
        binding.txtLastName.setText(lastName)
        binding.txtAge.setText(age.toString())
        binding.txtGender.setText(gender)
        binding.txtEmail.setText(email)
        binding.txtPhone.setText(phone)
        binding.txtUsername.setText(username)
    }

}