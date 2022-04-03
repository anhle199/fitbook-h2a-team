package com.h2a.fitbook.views.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.h2a.fitbook.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}
