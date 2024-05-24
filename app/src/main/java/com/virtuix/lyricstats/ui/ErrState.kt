package com.virtuix.lyricstats.ui

/**
 * Holds error information.  This consists of a
 * description of the error type suitable for devs
 * and a text message to display to users (if applicable).
 *
 * @param   isErr       This state is only meaningful if set
 *                      to true.
 *
 * @param   errDescId   Id of the string that describes the
 *                      error to be used for debugging log message.
 *                      0 means not used.
 *
 * @param   errMsgId    Id of error message suitable for users.
 *                      0 means not used.
 */
data class ErrState(
    val isErr : Boolean = false,
    val errDescId : Int = 0,
    val errMsgId : Int = 0
)
