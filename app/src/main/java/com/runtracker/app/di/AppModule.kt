package com.runtracker.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.runtracker.app.db.RunningDatabase
import com.runtracker.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        RunningDatabase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDAO(db : RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context)  =
        context.getSharedPreferences(
            Constants.SHARED_PREF_NAME,
            Context.MODE_PRIVATE)
    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(Constants.KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(Constants.KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE,true)
}