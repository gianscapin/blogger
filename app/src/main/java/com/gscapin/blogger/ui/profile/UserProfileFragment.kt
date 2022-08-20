package com.gscapin.blogger.ui.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.databinding.FragmentUserProfileBinding
import com.gscapin.blogger.presentation.message.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding
    private val args by navArgs<UserProfileFragmentArgs>()
    val viewModel: MessageViewModel by viewModels()

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor= Color.parseColor("#E1F5FE")
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

        binding.sendMessage.setOnClickListener {
            viewModel.sendUserMessage(args.idUser).observe(viewLifecycleOwner, Observer { result ->
                when(result){
                    is Result.Loading -> {
                        binding.progressBarSendMessage.show()
                    }
                    is Result.Success -> {
                        binding.progressBarSendMessage.hide()
                        goToMessagesScreen()
                    }
                    is Result.Failure -> {
                        binding.progressBarSendMessage.hide()
                        Toast.makeText(
                            requireContext(),
                            "Error go to message screen ${result.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    private fun goToMessagesScreen() {
        val action = UserProfileFragmentDirections.actionUserProfileFragmentToMessageUserFragment(
            idUser = args.idUser,
            photoUser = args.photoUrl,
            nameUser = args.nameUser
        )
        findNavController().navigate(action)
    }
}