package com.gscapin.blogger.ui.auth.create

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
import com.gscapin.blogger.databinding.FragmentCreateAccountBinding
import com.gscapin.blogger.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private lateinit var binding: FragmentCreateAccountBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateAccountBinding.bind(view)

        binding.toolbarCreateAccount.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnCreate.isEnabled = false
        validateDates()
        actionInput()
    }


    private fun validateDates() {

        binding.btnCreate.setOnClickListener {
            val fullName = binding.nameInputCreateAccount.text.toString().trim()
            val email = binding.emailInputCreateAccount.text.toString().trim()
            val password = binding.passwordInputCreateAccount.text.toString().trim()
            val passwordValidate = binding.validatePasswordInputCreateAccount.text.toString().trim()

            if (fullName.isEmpty()) {
                binding.nameInputCreateAccount.error = "Nombre obligatorio"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailInputCreateAccount.error = "Email obligatorio"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInputCreateAccount.error = "Contraseña obligatoria"
                return@setOnClickListener
            }

            if (passwordValidate.isEmpty()) {
                binding.validatePasswordInputCreateAccount.error = "Es obligatoria"
                return@setOnClickListener
            }

            if (password != passwordValidate) {
                binding.passwordInputCreateAccount.error = "Las contraseñas tienen que ser iguales"
                binding.validatePasswordInputCreateAccount.error =
                    "Las contraseñas tienen que ser iguales"
                return@setOnClickListener
            }

            registerAccount(fullName, email, password)
        }

    }

    private fun registerAccount(
        fullName: String,
        email: String,
        password: String,
    ) {
        viewModel.signUp(email = email, username = fullName, password = password)
            .observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.btnCreate.isEnabled = false
                        binding.progressCreateAccount.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressCreateAccount.visibility = View.GONE
                        findNavController().navigate(R.id.action_createAccountFragment_to_setupProfileFragment)
                    }
                    is Result.Failure -> {
                        binding.btnCreate.isEnabled = true
                        binding.progressCreateAccount.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error ${result.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun activateBtnCreate() {
        binding.btnCreate.isEnabled = true
    }

    private fun actionInput() {
        binding.validatePasswordInputCreateAccount.setOnEditorActionListener { v, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                activateBtnCreate()
                false
            } else false
        }
    }
}