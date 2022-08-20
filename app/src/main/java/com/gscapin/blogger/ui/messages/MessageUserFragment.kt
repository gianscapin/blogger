package com.gscapin.blogger.ui.messages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentMessageUserBinding


class MessageUserFragment : Fragment(R.layout.fragment_message_user) {

    private lateinit var binding: FragmentMessageUserBinding
    private val args by navArgs<MessageUserFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMessageUserBinding.bind(view)

        binding.toolbarLogin.title = args.nameUser
        Glide.with(requireContext()).load(args.photoUser).centerCrop().into(binding.photoUser)
        binding.toolbarLogin.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}