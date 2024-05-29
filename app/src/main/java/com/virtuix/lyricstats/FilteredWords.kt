package com.virtuix.lyricstats

import android.content.Context

/**
 * Set of functions to determine if a word should be filtered
 * out for this project.
 *
 * Currently words that should be filtered out:
 *      prepositions
 *      interjections
 *      articles
 *
 * NOTE
 *      Things that are not normally considered words will be
 *      left untouched.  "S**t", "$48.99", and even
 *      "------------------" are all ok words here.
 *      This class does not do a dictionary check--you'll have
 *      to do that elsewhere.
 */
class FilteredWords(val ctx : Context) {

    //-----------------------------
    //  data
    //-----------------------------

    private val articleList : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.articles)
    }

    private val prepositionList : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.prepositions)
    }

    private val interjectionList : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.interjections)
    }

    //-----------------------------
    //  functions
    //-----------------------------

    /**
     * Opposite of [shouldBeFiltered].  Tests to see if a word
     * should be left alone--not filtered.
     */
    fun unfiltered(word : String) : Boolean = !shouldBeFiltered(word)


    /**
     * Tests to see if the word should be filtered out.
     *
     * @return      True  - matches our bad word list--filter that guy out!
     *              False - this word is ok, go ahead and keep it.
     */
    fun shouldBeFiltered(word: String) : Boolean {
        if (isArticle(word) or isPreposition(word) or isInterjection(word)) {
            return true
        }
        return false
    }


    fun isArticle(word : String) : Boolean =
        articleList.contains(word)

    fun isPreposition(word : String) : Boolean =
        prepositionList.contains(word)

    fun isInterjection(word : String) : Boolean =
        interjectionList.contains(word)

}