package com.example.gsdemo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.gsdemo.utils.PreferenceHelper

/**
 * The MyApplication class, base class of application
 */
class MyApplication : Application() {
    companion object {
        /**
         * The prefHelper, instance of SharedPreferences
         */
        var prefHelper: SharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        getPreferenceInstance(this)
    }


    /**
     * The getPreferenceInstance method, to get singleton instance of SharedPreferences
     */
    private fun getPreferenceInstance(base: Context): SharedPreferences {
        if (prefHelper == null) {
            prefHelper = PreferenceHelper.defaultPrefs(base)
        }
        return prefHelper as SharedPreferences
    }

}