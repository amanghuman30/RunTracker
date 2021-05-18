package com.runtracker.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.runtracker.app.R
import com.runtracker.app.services.PolyLine
import com.runtracker.app.services.TrackingService
import com.runtracker.app.util.Constants
import com.runtracker.app.util.TrackingUtility
import com.runtracker.app.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val mainViewModel : MainViewModel by viewModels()
    private var map : GoogleMap? = null

    private var isTracking = false
    private var pathList = mutableListOf<PolyLine>()

    private var currentTimeInMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        mapView.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathList = it
            addLatestPolyLine()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            tvTimer.text = formattedTime
        })
    }

    private fun toggleRun() {
        if(isTracking) {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking : Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if(pathList.isNotEmpty() && pathList.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathList.last().last(),
            Constants.MAP_ZOOM))
        }
    }

    private fun addAllPolylines() {
        for(polyline in pathList) {
            val polyLineOptions = PolylineOptions().apply {
                color(Constants.POLYLINE_COLOR)
                width(Constants.POLYLINE_WIDTH)
                addAll(polyline)
            }
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun addLatestPolyLine() {
        if(pathList.isNotEmpty() && pathList.last().size > 1) {
            val preLastLatlng = pathList.last()[pathList.last().size-2]
            val lastLatlng = pathList.last().last()
            val polyLineOptions = PolylineOptions().apply {
                color(Constants.POLYLINE_COLOR)
                width(Constants.POLYLINE_WIDTH)
                add(preLastLatlng)
                add(lastLatlng)
            }

            map?.addPolyline(polyLineOptions)
        }
    }

    private fun sendCommandToService(action : String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}