package com.gscapin.blogger.ui.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gscapin.blogger.R
import com.gscapin.blogger.databinding.FragmentUserPreferencesBinding


@Suppress("DEPRECATION")
class UserPreferencesFragment : Fragment(R.layout.fragment_user_preferences) {
    private lateinit var binding: FragmentUserPreferencesBinding
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserPreferencesBinding.bind(view)

        binding.toolbarUser.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNightMode.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //activity?.setTheme(R.style.darkModeBlogger_Blogger)
                //activity?.window?.statusBarColor = Color.parseColor("#000000")
                //activity?.window?.decorView?.systemUiVisibility = View.STATUS_BAR_VISIBLE
                activity?.window?.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    statusBarColor = Color.TRANSPARENT
                }
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                activity?.window?.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    statusBarColor = Color.TRANSPARENT
                }
            }
        }
    }
}