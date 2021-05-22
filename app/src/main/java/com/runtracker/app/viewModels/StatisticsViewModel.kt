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

    val totalTimeRun = runRepository.getTotalTimeInMillis()
    val totalAvgSpeed = runRepository.getTotalAvgSpeed()
    val totalDistanceRunInMeters = runRepository.getTotalDistanceInMeters()
    val totalCaloriesBurned = runRepository.getTotalCaloriesBurned()

    val runSortedByDate = runRepository.getAllRunsSortedByDate()
}