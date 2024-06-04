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
 * @param	wordList			Holds all the words in the given song.  Only valid when
 * 								thinking == false.  Defaults to empty list.
 */
data class MainUiState(
	val artist: String = "",
	val songTitle: String = "",
	val processChoice: Boolean = false,
	val currentWord: String = "",
	val definition: AnnotatedString = AnnotatedString(""),
	val thinking: Boolean = false,
	val wordList: List<String> = listOf()
)