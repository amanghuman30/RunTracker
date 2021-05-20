package com.runtracker.app.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateRun(run : Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("Select * from running_table order by timeStamp desc")
    fun getAllRunsSortByDate() : LiveData<List<Run>>

    @Query("Select * from running_table order by avgSpeedKMH desc")
    fun getAllRunsSortByAvgSpeed() : LiveData<List<Run>>

    @Query("Select * from running_table order by distanceInMeters desc")
    fun getAllRunsSortByDistance() : LiveData<List<Run>>

    @Query("Select * from running_table order by caloriesBurned desc")
    fun getAllRunsSortByCaloriesBurned() : LiveData<List<Run>>

    @Query("Select * from running_table order by timeInMillis desc")
    fun getAllRunsSortByTimeInMillis() : LiveData<List<Run>>

    @Query("Select sum(timeInMillis) from running_table")
    fun getTotalTimeInMillis() : LiveData<Long>

    @Query("Select sum(caloriesBurned) from running_table")
    fun getTotalCaloriesBurned() : LiveData<Int>

    @Query("Select sum(distanceInMeters) from running_table")
    fun getTotalDistanceInMeters() : LiveData<Int>

    @Query("Select avg(avgSpeedKMH) from running_table")
    fun getTotalAvgSpeed() : LiveData<Float>
}