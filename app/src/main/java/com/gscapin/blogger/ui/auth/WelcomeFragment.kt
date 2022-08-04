package com.gscapin.blogger.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentLoginBinding
import com.gscapin.blogger.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWelcomeBinding.bind(view)

        goToCreateAccount()

        goToLogin()

        isUserLog()

    }

    private fun isUserLog() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }

    private fun goToLogin() {
        binding.btnLoginAccount.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }

    private fun goToCreateAccount() {
        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_createAccountFragment)
        }
    }
}