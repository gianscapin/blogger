package com.gscapin.blogger.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.databinding.FragmentMessageUserBinding
import com.gscapin.blogger.presentation.message.MessageViewModel
import com.gscapin.blogger.ui.messages.adapter.ChatMessageAdapter
import com.gscapin.blogger.ui.messages.adapter.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class MessageUserFragment : Fragment(R.layout.fragment_message_user) {

    private lateinit var binding: FragmentMessageUserBinding
    private val args by navArgs<MessageUserFragmentArgs>()
    val viewModel: MessageViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMessageUserBinding.bind(view)


        binding.toolbarLogin.title = args.nameUser
        Glide.with(requireContext()).load(args.photoUser).centerCrop().into(binding.photoUser)
        binding.toolbarLogin.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendMessageInput.addTextChangedListener {
            binding.sendMessageButton.isEnabled = binding.sendMessageInput.text?.length!! > 0
        }

        binding.sendMessageButton.setOnClickListener {
            val text = binding.sendMessageInput.text.toString()
            binding.sendMessageInput.setText("")
            viewModel.sendMessage(text = text, idChat = args.idChat!!).observe(viewLifecycleOwner,
                Observer { result ->
                    when (result) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "Mensaje agregado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Error ${result.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("chat", "${result.exception}")
                        }
                    }
                })
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.lastestMessages(args.idChat!!).collect{ result ->
//                    when(result){
//                        is Result.Loading -> {
//
//                        }
//                        is Result.Success -> {
//                            if(result.data.isEmpty()){
//                                return@collect
//                            }
//                            binding.recyclerView.adapter = ChatMessageAdapter(result.data)
//                        }
//                        is Result.Failure -> {
//                            Toast.makeText(
//                                requireContext(),
//                                "Error ${result.exception}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.d("chat", "${result.exception}")
//                        }
//                    }
//                }
//            }
//        }
        viewModel.latestMessages(idChat = args.idChat!!)

        lifecycleScope.launchWhenCreated {
            getMessagesStateFlow()
        }
    }


    private suspend fun getMessagesStateFlow() {
        viewModel.getMessages().collect { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        return@collect
                    }
                    val chatAdapter: ChatMessageAdapter = ChatMessageAdapter(result.data)
                    binding.recyclerView.adapter = chatAdapter
                    binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
                is Result.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Error ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("chat", "${result.exception}")
                }
            }
        }
    }
}