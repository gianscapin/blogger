package com.gscapin.blogger.ui.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding
    private val args by navArgs<UserProfileFragmentArgs>()

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserProfileBinding.bind(view)

        binding.toolbarUser.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.nameUser.text = args.nameUser
        Glide.with(requireContext()).load(args.photoUrl).centerCrop().into(binding.photoUser)

        binding.toolbarUser.title = args.nameUser

        binding.addFriend.setOnClickListener {
            val textBtn = binding.addFriend.text
            if(textBtn.equals("Following")){
                binding.addFriend.setBackgroundColor(Color.WHITE)
                binding.addFriend.text = "Follow"
                binding.addFriend.setTextColor(Color.BLACK)
            }else{
                binding.addFriend.setBackgroundColor(Color.parseColor("#F48FB1"))
                binding.addFriend.text = "Following"
                binding.addFriend.setTextColor(Color.BLACK)
            }

        }
    }
}