package com.virtuix.lyricstats.dictionary

/**
 * Use this to check a words for their meaning, parts of speech, etc.
 * It has a cache of words that were recently checked.  If a word is
 * not in the cache, it searches https://dictionapi.dev to find the
 * information.
 *
 * todo: lots!!!
 */
class Dictionary {

    private val dict = mutableMapOf<String, DictionaryEntry>()


    init {
        // todo
    }

    /**
     * Returns the definition of the given word.  Returns
     * an empty string if the word can't be found in the
     * dictionary.
     */
    fun getDefinition(word : String) : String {
        // todo
        return word
    }

    /**
     * Curious to see if this word is forbidden?  This function
     * is your guy.  Returns TRUE if the given word is
     *      a preposition
     *      an article
     *      an interjection
     */
    fun isForbiddenType(word : String) : Boolean {
        // todo
        return false
    }

}
