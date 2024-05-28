package com.virtuix.lyricstats

import android.content.Context

/**
 * The different types of errors that the ErrState can handle.
 *
 * The ErrorStateType will indicate which of the properties are
 * valid.  So if the type is WORD, then the [ErrState.word] property
 * will contain information that should be used in conjunction with
 * the string specified by the r.string.id.
 *
 * None means that no lookup is needed (probably the errState will be false too).
 */
enum class ErrStateType {
    ARTIST, TITLE, ARTIST_AND_TITLE, WORD, NONE
}

/**
 * Holds error information.  This consists of a
 * description of the error type suitable for devs
 * and a text message to display to users (if applicable).
 *
 * @param   errType     An enum of [ErrStateType].  Tells which type
 *                      of parameter to use when constructing the error
 *                      message.
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
 *
 * @param   word        A word to fill in as a parameter to the error string.
 */
data class ErrState(
    val errType : ErrStateType,
    val errState : Boolean = false,
    val errDescId : Int = 0,
    val errMsgId : Int = 0,
    val artist : String = "",
    val title : String = "",
    val word : String = ""
) {
    /**
     * Returns the description string (for logging) associated with current
     * error state.
     *
     * Returns empty string if errState is false or there is no appropriate
     * string resource
     */
    fun getDescString(ctx : Context) : String {
        if ((errState == false) or (errDescId == 0)) {
            return ""
        }

        return when (errType) {
            ErrStateType.ARTIST -> {
                ctx.getString(errDescId, artist)
            }
            ErrStateType.TITLE -> {
                ctx.getString(errDescId, title)
            }
            ErrStateType.ARTIST_AND_TITLE -> {
                ctx.getString(errDescId, artist, title)
            }
            ErrStateType.WORD -> {
                ctx.getString(errDescId, word)
            }
            ErrStateType.NONE -> {
                ctx.getString(errDescId)
            }
        }
    }

    /**
     * Similar to [getDescString], returns a string suitable for users.
     */
    fun getMsgString(ctx : Context) : String {
        if ((errState == false) or (errMsgId == 0)) {
            return ""
        }

        return when (errType) {
            ErrStateType.ARTIST -> {
                ctx.getString(errMsgId, artist)
            }
            ErrStateType.TITLE -> {
                ctx.getString(errMsgId, title)
            }
            ErrStateType.ARTIST_AND_TITLE -> {
                ctx.getString(errMsgId, artist, title)
            }
            ErrStateType.WORD -> {
                ctx.getString(errMsgId, word)
            }
            ErrStateType.NONE -> {
                ctx.getString(errMsgId)
            }
        }
    }

}
