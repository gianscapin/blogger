package com.gscapin.blogger.ui.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentMessageBinding
import com.gscapin.blogger.ui.home.HomeFragment

class MessageFragment : Fragment(R.layout.fragment_message) {

    private lateinit var binding: FragmentMessageBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMessageBinding.bind(view)
    }
}