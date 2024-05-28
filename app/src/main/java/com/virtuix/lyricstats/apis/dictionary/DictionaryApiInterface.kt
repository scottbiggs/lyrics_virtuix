package com.virtuix.lyricstats.apis.dictionary

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Defines interface for grabbing definitions from an on-line
 * dictionary.
 */
interface DictionaryApiInterface {

    /**
     * GETs a definition of a word.
     *
     *      todo!!!
     * @param	language	@Get - The language abbreviation (en = english)
     *
     * @param	word		@Get - Word to look up
     *
     * @return	A list (with just one item) of dictionary entries
     */
    // todo: make localiztion work
//    @GET("https://api.dictionaryapi.dev/api/v2/entries/{language}/{word}")
    @GET("{word}")
    suspend fun dictLookup(@Path("word") word: String): Response<List<DictionaryEntry>>

}
