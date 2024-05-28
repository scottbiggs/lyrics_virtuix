package com.virtuix.lyricstats.apis.dictionary

data class DictionaryEntry(
    val license: License,
    val meanings: List<Meaning>,
    val phonetic: String,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String
) {
    //-------------------
    //  functions to check word type
    //-------------------

    fun isWord() : Boolean {
        if (meanings.size == 0) {
            return false
        }
        return true
    }

}