package com.virtuix.lyricstats

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
 */
data class MainUiState(
	val artist: String = "",
	val songTitle: String = "",
	val processChoice: Boolean = false,
	val currentWord: String = ""
)