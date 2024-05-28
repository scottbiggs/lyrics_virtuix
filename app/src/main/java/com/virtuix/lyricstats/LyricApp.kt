package com.virtuix.lyricstats

import android.app.Application

/**
 * Deep level application class.  Needed to access strings so
 * localization will work through the viewmodel.
 */
class LyricApp : Application() {

    lateinit var instance : LyricApp
        private set


    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    /**
     * Use to get an application context from just about anywhere
     */
    fun getContext() = { instance.applicationContext }

    /**
     * Just in case you need an application instance...
     */
    fun getInstance() = { instance }
}
