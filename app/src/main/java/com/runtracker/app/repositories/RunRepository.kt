package com.runtracker.app.repositories

import com.runtracker.app.db.Run
import com.runtracker.app.db.RunDAO
import javax.inject.Inject

class RunRepository @Inject constructor(
    private val runDAO: RunDAO
) {
    suspend fun insertRun(run: Run) = runDAO.insertUpdateRun(run)

    suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortByDate()

    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortByDistance()

    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortByCaloriesBurned()

    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortByTimeInMillis()


    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()

    fun getTotalDistanceInMeters() = runDAO.getTotalDistanceInMeters()

    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
}