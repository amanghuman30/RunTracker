package com.runtracker.app.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.runtracker.app.R
import com.runtracker.app.viewModels.MainViewModel
import com.runtracker.app.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val statisticsViewModel : StatisticsViewModel by viewModels()
}