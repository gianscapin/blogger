package com.gscapin.blogger.ui.posting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentPostingBinding

class PostingFragment : Fragment(R.layout.fragment_posting) {

    private lateinit var binding: FragmentPostingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostingBinding.bind(view)
    }
}