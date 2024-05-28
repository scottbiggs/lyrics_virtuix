package com.virtuix.lyricstats.apis.dictionary

import android.content.Context
import com.virtuix.lyricstats.R

data class DictionaryEntry(
    val license: License,
    val meanings: List<Meaning>,
    val phonetic: String,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String,
    val ctx : Context
) {

    private val articleList : Array<String> = ctx.resources.getStringArray(R.array.articles)
    private val prepositionList : Array<String>
    private val interjectionList : Array<String>


    //-------------------
    //  functions to check word type
    //-------------------

    fun isWord() : Boolean {
        if (meanings.size == 0) {
            return false
        }
        return true
    }

    fun isArticle() : Boolean {
        if (meanings[0].partOfSpeech == "article") {
            return true
        }
        return false
    }

}