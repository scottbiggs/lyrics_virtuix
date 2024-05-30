package com.virtuix.lyricstats

import android.content.Context

/**
 * Interface for LyricApp.  Necessary for testing.
 */
interface LyricAppInterface {

    fun getLyricAppInstance(): LyricApp

    fun getLyricAppContext(): Context

}