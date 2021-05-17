package com.runtracker.app.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.runtracker.app.util.Constants
import timber.log.Timber

class TrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Action resume or start service")
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    Timber.d("Action pause service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Action stop service")
                }
            }

        }

        return super.onStartCommand(intent, flags, startId)
    }

}