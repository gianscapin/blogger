package com.gscapin.blogger.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.core.hideKeyboard
import com.gscapin.blogger.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        binding.toolbarLogin.setNavigationOnClickListener { findNavController().popBackStack() }

        actionInput()
        loginAccount()
    }

    private fun loginAccount() {
        binding.btnLogin.setOnClickListener {
            it.hideKeyboard()
            //findNavController().navigate()
        }
    }

    private fun actionInput(){
        binding.btnLogin.isEnabled = false
        binding.passwordInput.setOnEditorActionListener { v, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE){
                activateButton()
                false
            }else false
        }

    }

    private fun activateButton(){
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        if(!email.isEmpty() && !password.isEmpty()){
            binding.btnLogin.isEnabled = true
            binding.btnLogin.isActivated = true
            binding.btnLogin.isClickable = true
        }
    }
}