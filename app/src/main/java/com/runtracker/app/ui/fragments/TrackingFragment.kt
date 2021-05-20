package com.runtracker.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.runtracker.app.R
import com.runtracker.app.db.Run
import com.runtracker.app.services.PolyLine
import com.runtracker.app.services.TrackingService
import com.runtracker.app.util.Constants
import com.runtracker.app.util.TrackingUtility
import com.runtracker.app.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val mainViewModel : MainViewModel by viewModels()
    private var map : GoogleMap? = null

    private var weight = 80

    private var isTracking = false
    private var pathList = mutableListOf<PolyLine>()

    private var currentTimeInMillis = 0L

    private var menu : Menu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        btnFinishRun.setOnClickListener {
            endRunAndSaveInDb()
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if(currentTimeInMillis > 0) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the run and delete all data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){dialogInterface, id ->
                stopRun()
            }
            .setNegativeButton("No") {dialogInterface, id ->
                dialogInterface.cancel()
            }
            .create()

        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun toggleRun() {
        if(isTracking) {
            menu?.getItem(0)?.isVisible = true
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
            menu?.getItem(0)?.isVisible = true
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

    private fun zoomToSeeWholeRoute() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathList) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()))
    }

    private fun endRunAndSaveInDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for(polyLine in pathList) {
                distanceInMeters += TrackingUtility.calculatePolyLineLength(polyLine).toInt()
            }
            val avgSpeed = round((distanceInMeters/1000f) / (currentTimeInMillis/1000f/60/60)) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters/ 1000f) * weight).toInt()
            val run = Run(bmp, dateTimeStamp, avgSpeed, distanceInMeters, currentTimeInMillis, caloriesBurned)

            mainViewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run Saved Successfully!",
                Snackbar.LENGTH_SHORT
            ).show()

            stopRun()
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