package com.runtracker.app.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtracker.app.db.Run
import com.runtracker.app.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val runRepository : RunRepository) : ViewModel() {

    fun insertRun(run :Run) = viewModelScope.launch {
        runRepository.insertRun(run)
    }

}