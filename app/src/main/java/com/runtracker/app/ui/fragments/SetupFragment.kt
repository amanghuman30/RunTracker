package com.runtracker.app.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.runtracker.app.R
import com.runtracker.app.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_run_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_run_setup) {

    @Inject
    lateinit var sharedPref : SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.setupFragment, true)
            .build()

        if(!isFirstAppOpen) {
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions)
        }

        tvContinue.setOnClickListener {
            val success = saveDataToSharedPrefs()
            if(success)
                findNavController().navigate(R.id.action_setupFragment_to_runFragment, savedInstanceState, navOptions)
            else
                Snackbar.make(requireView(), "Please fill all the fields!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToSharedPrefs() : Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()

        if(name.isEmpty() || weight.isEmpty())
            return false

        sharedPref.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "Let's go, $name"
        requireActivity().toolBar.title = toolbarText
        return true
    }

}