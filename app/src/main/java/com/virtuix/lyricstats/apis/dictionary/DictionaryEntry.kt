package com.virtuix.lyricstats.apis.dictionary

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.Log
import com.virtuix.lyricstats.R

data class DictionaryEntry(
    val license: License,
    val meanings: List<Meaning>,
    val phonetic: String,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String,
) {

//    private var articleList : Array<String>? = null
//    private var prepositionList : Array<String>? = null
//    private var interjectionList : Array<String>? = null


    //-------------------
    //  functions to check word type
    //-------------------

    fun isWord() : Boolean {
        if (meanings.isEmpty()) {
            return false
        }
        return true
    }

//    fun isArticle(ctx: Context) : Boolean {
//        if (articleList == null) {
//            articleList = ctx.resources.getStringArray(R.array.articles)
//        }
//
//        // Try to find the word in the article list
//        articleList?.let {
//            return it.contains(word)
//        }
//
//        // if we made it this far, this means that articleList is STILL null.
//        Log.e(TAG, "unable to find article list in isArticle()!!! - aborting!")
//        throw NotFoundException()
//    }

//    fun isPreposition(ctx: Context) : Boolean {
//        if (prepositionList == null) {
//            prepositionList = ctx.resources.getStringArray(R.array.prepositions)
//        }
//
//        prepositionList?.let {
//            return it.contains(word)
//        }
//        // if we made it this far, this means that articleList is STILL null.
//        Log.e(TAG, "unable to find preposition list in isPreposition()!!! - aborting!")
//        throw NotFoundException()
//    }

//    fun isInterjection(ctx: Context) : Boolean {
//        if (interjectionList == null) {
//            interjectionList = ctx.resources.getStringArray(R.array.interjections)
//        }
//
//        interjectionList?.let {
//            return it.contains(word)
//        }
//        // if we made it this far, this means that articleList is STILL null.
//        Log.e(TAG, "unable to find preposition list in isInterjection()!!! - aborting!")
//        throw NotFoundException()
//    }

}

const val TAG = "DictionaryEntry"