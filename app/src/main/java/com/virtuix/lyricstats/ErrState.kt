package com.virtuix.lyricstats

import android.content.Context

/**
 * Holds error information.  This consists of a
 * description of the error type suitable for devs
 * and a text message to display to users (if applicable).
 *
 * @param   errState    This state is only meaningful if set
 *                      to true.  When false, there is no error.
 *
 * @param   errDescId   Id of the string that describes the
 *                      error to be used for debugging log message.
 *                      0 means not used.
 *
 * @param   errMsgId    Id of error message suitable for users.
 *                      0 means not used.
 *
 * @param   artist      The name of the artist (if needed).  This is only
 *                      relevant if the description string requires the
 *                      name of the artist.  Defaults to empty string.
 *
 * @param   title       The name of the song title if relevant.  Sometimes
 *                      used by the error message strings.
 */
data class ErrState(
    val errState : Boolean = false,
    val errDescId : Int = 0,
    val errMsgId : Int = 0,
    val artist : String = "",
    val title : String = ""
) {
    /**
     * Returns the description string (for logging) associated with current
     * error state.
     *
     * Returns empty string if errState is false or there is no appropriate
     * string resource
     */
    fun getDescString(ctx : Context) : String {
        if (errState && (errDescId != 0)) {
            return ctx.getString(errDescId, artist, title)
        }
        return ""
    }

    /**
     * Similar to [getDescString], returns a string suitable for users.
     */
    fun getMsgString(ctx : Context) : String {
        if (errState && (errMsgId != 0)) {
            return ctx.getString(errMsgId, artist, title)
        }
        return ""
    }

}
