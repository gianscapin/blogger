package com.gscapin.blogger.ui.auth.create

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.databinding.FragmentSetupProfileBinding
import com.gscapin.blogger.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {

    private lateinit var binding: FragmentSetupProfileBinding

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.addPhoto.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
            }
        }

    private var bitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: AuthViewModel by viewModels()
        binding = FragmentSetupProfileBinding.bind(view)

        binding.addPhoto.setOnClickListener {
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

        binding.btnCreateProfile.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).setTitle("Uploading photo...").create()
            if(bitmap != null){
                viewModel.updateProfile(bitmap!!).observe(viewLifecycleOwner, Observer { result ->
                    when(result){
                        is Result.Loading -> {
                            alertDialog.show()
                        }
                        is Result.Success -> {
                            alertDialog.hide()
                            findNavController().navigate(R.id.action_setupProfileFragment_to_homeFragment)
                        }
                        is Result.Failure -> {
                            alertDialog.hide()
                            Toast.makeText(requireContext(), "Error ${result.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

    }
}