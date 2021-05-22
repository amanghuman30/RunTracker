package com.runtracker.app.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.runtracker.app.R
import com.runtracker.app.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFromSharedPrefs()

        btnApplyChanges.setOnClickListener {
            if(applyChangesToSharedPref())
                Snackbar.make(requireView(), "Changes saved successfully", Snackbar.LENGTH_SHORT).show()
            else
                Snackbar.make(requireView(), "Please fill both fields!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadFromSharedPrefs() {
        val name = sharedPreferences.getString(Constants.KEY_NAME,"")
        val weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref() : Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()

        if(name.isNotEmpty() && weight.isNotEmpty()) {
            sharedPreferences.edit()
                .putString(Constants.KEY_NAME, name)
                .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
                .apply()

            val toolbarText = "Let's go $name!"
            requireActivity().toolBar.title = toolbarText
            return true
        }
        return false
    }
}