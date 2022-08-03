package com.gscapin.blogger.ui.auth.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentCreateAccountBinding

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private lateinit var binding: FragmentCreateAccountBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateAccountBinding.bind(view)

        binding.toolbarCreateAccount.setNavigationOnClickListener { findNavController().popBackStack() }

        validateBtnCreateAccount()
    }


    private fun validateBtnCreateAccount() {
        val fullName = binding.nameInputCreateAccount.text
        val email = binding.emailInputCreateAccount.text
        val password = binding.passwordInputCreateAccount.text
        val passwordValidate = binding.validatePasswordInputCreateAccount.text

        val valid = password?.equals(passwordValidate) == true && (!password.isNullOrEmpty() && !passwordValidate.isNullOrEmpty())

        if(valid){
            if(!fullName.isNullOrEmpty() && !email.isNullOrEmpty()){
                binding.btnCreate.isEnabled = true
            }
        }
    }
}