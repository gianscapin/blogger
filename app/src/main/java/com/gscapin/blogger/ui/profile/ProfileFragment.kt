package com.gscapin.blogger.ui.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.databinding.FragmentProfileBinding
import com.gscapin.blogger.presentation.auth.AuthViewModel
import com.gscapin.blogger.presentation.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    val viewModel: ProfileViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.photoUser.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

    private var bitmap: Bitmap? = null

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
                    if(result.data.userPhotoUrl == ""){
                        val image = R.drawable.ic_baseline_add_a_photo_24
                        context?.let { Glide.with(it).load(image).centerInside().into(binding.photoUser) }
                        binding.photoUser.setOnClickListener {
                            takePhoto()
                            binding.savePhotoUser.show()
                        }
                    }else{
                        context?.let { Glide.with(it).load(result.data.userPhotoUrl).centerCrop().into(binding.photoUser) }
                    }

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

        binding.savePhotoUser.setOnClickListener {
            authViewModel.updateProfile(bitmap!!).observe(viewLifecycleOwner, Observer { result ->
                when(result){
                    is Result.Loading -> {
                        binding.progressSavePhoto.show()
                    }
                    is Result.Success -> {
                        binding.progressSavePhoto.hide()
                        binding.savePhotoUser.hide()
                        Toast.makeText(
                            requireContext(),
                            "Foto de perfil actualizada.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Failure -> {
                        binding.progressSavePhoto.hide()
                        binding.savePhotoUser.hide()
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

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            getResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No se encontró la app para abrir la cámara",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}