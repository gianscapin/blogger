package com.gscapin.blogger.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.databinding.FragmentHomeBinding
import com.gscapin.blogger.presentation.home.HomeViewModel
import com.gscapin.blogger.ui.home.adapter.HomeAdapter
import com.gscapin.blogger.ui.home.adapter.OnPostClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), OnPostClickListener {

    private lateinit var binding: FragmentHomeBinding
    val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding = FragmentHomeBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLastestPosts().observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (result.data.isEmpty()) {
                            binding.postsEmpty.visibility = View.VISIBLE
                            return@Observer
                        } else {
                            binding.postsEmpty.visibility = View.GONE
                        }
                        binding.rvHome.adapter = HomeAdapter(result.data, this@HomeFragment)
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error ${result.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Error posts", "${result.exception}")
                    }
                }
            })
        }

        logOut()
    }

    private fun logOut() {
        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_homeFragment_to_welcomeFragment)
        }
    }


    override fun onLikeButtonClick(post: Post, liked: Boolean) {
        viewModel.likePost(postId = post.id, liked = liked).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Result.Loading -> {}
                is Result.Success -> {}
                is Result.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Error like post ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}