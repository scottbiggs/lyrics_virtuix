package com.virtuix.lyricstats

import androidx.compose.ui.text.AnnotatedString

/**
 * @param	artist				The name of the artist
 *
 * @param	songTitle			Song to process.  Can contain odd characters.
 *
 * @param	processChoice		True -> to process the lyrics
 * 								to find the most common word.
 * 								False -> process to find longest word
 *
 * @param	currentWord			The word that is the result of the processing.
 *
 * @param	definition			Definition associated with currentWord.
 *
 * @param	thinking			When True, the app is processing (could take a while).
 *
 * @param	wordMap 			Holds all the unique words in the given song and its count.
 * 								Only valid when thinking == false.  Defaults to empty list.
 */
data class MainUiState(
	val artist: String = "",
	val songTitle: String = "",
	val processChoice: Boolean = false,
	val currentWord: String = "",
	val definition: AnnotatedString = AnnotatedString(""),
	val thinking: Boolean = false,
	val wordMap: Map<String, Int> = mapOf()
)