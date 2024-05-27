package com.virtuix.lyricstats.dictionary

import kotlinx.serialization.Serializable


/**
 * Describes an entry in a dictionary.
 *
 * @param   rawJsonStr      Construct via a JSON String.
 *                          This is probably the string returned
 *                          from a web dictionary api.
 *
 * todo: so much to do here!
 */
@Serializable
data class DictionaryEntry(
    val rawJsonStr : String
)

