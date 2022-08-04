package com.gscapin.blogger.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hideKeyboard
import com.gscapin.blogger.databinding.FragmentLoginBinding
import com.gscapin.blogger.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: AuthViewModel by viewModels()

        binding = FragmentLoginBinding.bind(view)

        binding.toolbarLogin.setNavigationOnClickListener { findNavController().popBackStack() }

        actionInput()
        loginAccount(viewModel)
    }

    private fun loginAccount(viewModel: AuthViewModel) {
        binding.btnLogin.setOnClickListener {
            it.hideKeyboard()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            signIn(email, password, viewModel)
        }
    }

    private fun signIn(email: String, password: String, viewModel: AuthViewModel) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    if(result.data?.displayName.isNullOrEmpty()){
                        findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
                    }else{
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
                is Result.Failure -> {
                    binding.progressBarLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    Toast.makeText(requireContext(), "Error ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
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