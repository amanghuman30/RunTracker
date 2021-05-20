package com.runtracker.app.viewModels

import androidx.lifecycle.*
import com.runtracker.app.db.Run
import com.runtracker.app.repositories.RunRepository
import com.runtracker.app.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val runRepository : RunRepository) : ViewModel() {

    private val runsSortedByDate = runRepository.getAllRunsSortedByDate()
    private val runsSortedByAvgSpeed = runRepository.getAllRunsSortedByAvgSpeed()
    private val runsSortedByCaloriesBurned = runRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByDistance = runRepository.getAllRunsSortedByDistance()
    private val runsSortedByTimeInMillis = runRepository.getAllRunsSortedByTimeInMillis()

    val runsMediatorLiveData = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runsMediatorLiveData.addSource(runsSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let {
                    runsMediatorLiveData.value = it
                }
            }
        }

        runsMediatorLiveData.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let {
                    runsMediatorLiveData.value = it
                }
            }
        }

        runsMediatorLiveData.addSource(runsSortedByCaloriesBurned) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result?.let {
                    runsMediatorLiveData.value = it
                }
            }
        }

        runsMediatorLiveData.addSource(runsSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let {
                    runsMediatorLiveData.value = it
                }
            }
        }

        runsMediatorLiveData.addSource(runsSortedByTimeInMillis) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let {
                    runsMediatorLiveData.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runsSortedByDate.value?.let { runsMediatorLiveData.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let {
            runsMediatorLiveData.value = it
        }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runsMediatorLiveData.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runsMediatorLiveData.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let {
            runsMediatorLiveData.value = it
        }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        runRepository.insertRun(run)
    }
}