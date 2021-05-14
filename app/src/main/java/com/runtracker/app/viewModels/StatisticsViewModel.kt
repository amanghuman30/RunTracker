package com.runtracker.app.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.runtracker.app.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(runRepository : RunRepository) : ViewModel() {


}