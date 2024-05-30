package com.virtuix.lyricstats

import android.app.Application
import android.content.Context
import android.content.res.Configuration

/**
 * Deep level application class.  Needed to access strings so
 * localization will work through the viewmodel.
 */
class LyricApp : Application() {


    override fun onCreate() {
        instance = this
        context = instance.applicationContext
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        instance = this
        context = instance.applicationContext
        super.onConfigurationChanged(newConfig)
    }

    fun getLyricAppInstance() : LyricApp {
        return instance
    }

    fun getLyricAppContext() : Context {
        return context
    }


    /**
     * Allows viewmodel to access strings and arrays.  This project
     * isn't really big enough to warrant moving all the string and
     * lanuage manipulation to a seperate section with its own context.
     */
    companion object {
        lateinit var instance : LyricApp
            private set

        lateinit var context : Context
            private set
    }

}
