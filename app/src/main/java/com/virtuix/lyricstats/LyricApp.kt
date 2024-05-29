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


    // todo:
    // fixme:   whew, talk about smells bad--don't forget to do this right later!
    companion object {
        lateinit var instance : LyricApp
            private set

        lateinit var context : Context
            private set
    }

}
