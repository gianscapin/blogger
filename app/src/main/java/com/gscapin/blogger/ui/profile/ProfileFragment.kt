package com.gscapin.blogger.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.databinding.FragmentProfileBinding
import com.gscapin.blogger.presentation.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        viewModel.getUserInfo().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Result.Loading -> {
                    binding.progressInfo.show()
                }
                is Result.Success -> {
                    binding.progressInfo.hide()
                    binding.nameUser.text = result.data.username
                    context?.let { Glide.with(it).load(result.data.userPhotoUrl).centerCrop().into(binding.photoUser) }
                }
                is Result.Failure -> {
                    binding.progressInfo.hide()
                    Toast.makeText(
                        requireContext(),
                        "Error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}