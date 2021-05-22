package com.runtracker.app.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.runtracker.app.R

class CancelTrackingDialog : DialogFragment() {

    private var yesListener : (() -> Unit)? = null

    fun setYesListener(listener : (() -> Unit)) {
        this.yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the run and delete all data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){dialogInterface, id ->
                yesListener?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton("No") {dialogInterface, id ->
                dialogInterface.cancel()
            }
            .create()
    }
}