package com.gscapin.blogger.ui.posting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.databinding.FragmentPostingBinding
import com.gscapin.blogger.presentation.post.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostingFragment : Fragment(R.layout.fragment_posting) {

    private lateinit var binding: FragmentPostingBinding

    val viewModel: PostViewModel by viewModels()


    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.newPostPhoto.setImageBitmap(imageBitmap)
                binding.newPostPhoto.layoutParams.height = 800
                binding.newPostPhoto.layoutParams.width = 800
                binding.newPostPhoto.requestLayout()
                bitmap = imageBitmap
            }
        }

    private var bitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostingBinding.bind(view)

        binding.newPostPhoto.setOnClickListener {
            takePhoto()
            binding.newPost.show()
        }


        binding.newPost.setOnClickListener {
            val description = binding.textInputEditText.text.toString().trim()
            val imagePost = bitmap
            viewModel.uploadPost(description = description, image = imagePost!!).observe(viewLifecycleOwner, Observer { result ->
                when(result){
                    is Result.Loading -> {
                        binding.progressPost.show()
                    }
                    is Result.Success -> {
                        binding.progressPost.hide()
                        Toast.makeText(
                            requireContext(),
                            "Post subido",
                            Toast.LENGTH_SHORT
                        ).show()
                       // ir a home
                    }
                    is Result.Failure -> {
                        binding.progressPost.hide()
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