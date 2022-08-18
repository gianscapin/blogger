package com.gscapin.blogger.ui.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gscapin.blogger.R
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.core.hide
import com.gscapin.blogger.core.show
import com.gscapin.blogger.data.model.ContactMessage
import com.gscapin.blogger.databinding.FragmentMessageBinding
import com.gscapin.blogger.presentation.message.MessageViewModel
import com.gscapin.blogger.ui.home.HomeFragment
import com.gscapin.blogger.ui.messages.adapter.MessageAdapter
import com.gscapin.blogger.ui.messages.adapter.OnContactClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : Fragment(R.layout.fragment_message), OnContactClickListener {

    private lateinit var binding: FragmentMessageBinding
    val viewModel: MessageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMessageBinding.bind(view)

        getContactList()
    }

    private fun getContactList() {
        viewModel.getContactListFromCurrentUser().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if(result.data.isEmpty()){
                        binding.contactMessagesEmpty.show()
                        return@Observer
                    }else{
                        binding.contactMessagesEmpty.hide()
                    }

                    binding.rvContactMessages.adapter = MessageAdapter(result.data, this@MessageFragment)
                }
                is Result.Failure -> {
                    binding.progressBar.hide()
                }
            }
        })
    }

    override fun onContactClickListener(contactMessage: ContactMessage) {
        TODO("Not yet implemented")
    }

}