package com.gscapin.blogger.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.databinding.FragmentHomeBinding
import com.gscapin.blogger.presentation.home.HomeViewModel
import com.gscapin.blogger.ui.home.adapter.HomeAdapter
import com.gscapin.blogger.ui.home.adapter.OnNameClickListener
import com.gscapin.blogger.ui.home.adapter.OnPostClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.w3c.dom.Attr

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), OnPostClickListener, OnNameClickListener {

    private lateinit var binding: FragmentHomeBinding
    val viewModel: HomeViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //activity?.window?.statusBarColor= Color.parseColor("#FAFAFA")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //requireActivity().window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.black)
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference =  activity?.getSharedPreferences("POSTS_BLOG", Context.MODE_PRIVATE)
        var edit = sharedPreference?.edit()

        binding = FragmentHomeBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                getPosts(edit)
                viewModel.seeNewPosts.collect{result ->
                    when(result){
                        is Result.Loading -> {}
                        is Result.Success -> {
                            // usar shared preferences
                            val listPosts = getPostsSP(sharedPreference)
                            if(result.data.toString() != listPosts!!){
                                binding.refreshPosts.show()
                            }
                        }
                        is Result.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Error ${result.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }



        logOut()
        actionRefreshPosts(edit)
    }

    private fun actionRefreshPosts(edit: SharedPreferences.Editor?) {
        binding.refreshPosts.setOnClickListener {
            getPosts(edit)
            binding.refreshPosts.hide()
        }
    }

    private fun getPosts(edit: SharedPreferences.Editor?) {
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
                    binding.rvHome.adapter = HomeAdapter(result.data, this@HomeFragment, onNameClickListener = this@HomeFragment)
                    edit?.putString("postSP",result.data.toString())
                    edit?.commit()
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

    private fun logOut() {
        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_homeFragment_to_welcomeFragment)
        }
    }

    private fun getPostsSP(sharedPreference: SharedPreferences?): String? {
        val posts = sharedPreference?.getString("postSP","")
        return posts;
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

    override fun onNameButtonClick(post: Post) {
        Log.d("user", post.poster.toString())
        val action = post.poster?.uid?.let {
            HomeFragmentDirections.actionHomeFragmentToUserProfileFragment(
                idUser = it,
                photoUrl = post.poster.profilePicture,
                nameUser = post.poster.username
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }
}