package com.abiy.projectfirebase.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abiy.projectfirebase.R
import com.abiy.projectfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {

    lateinit var binding :ActivityMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}