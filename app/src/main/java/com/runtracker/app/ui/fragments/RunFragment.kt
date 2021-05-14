package com.runtracker.app.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.runtracker.app.R
import com.runtracker.app.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val mainViewModel : MainViewModel by viewModels()

}