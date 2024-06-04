package com.virtuix.lyricstats

import androidx.compose.ui.text.AnnotatedString

interface MainViewModelInterface {
	fun updateArtist(artist: String)
	fun updateSongTitle(songTitle: String)
	fun updateProcessChoice(choice: Boolean)
	fun lookUpAndProcessLyrics()

	/**
	 * Call this after an error has been signaled.  This can be
	 * used to reset the error condition.
	 *
	 * @param	wasErrorHandled		Was the error handled? then set this
	 * 								to true.  If the error should persist,
	 * 								this should be false. Defaults to true.
	 */
	fun processError(wasErrorHandled: Boolean = true)

	/**
	 * Looks up the definition of the given word.
	 *
	 * side effect
	 * 		The definition will be stored in [MainUiState.definition].
	 * 		This should cause recomposition.
	 */
	fun getDefinition(word: String) : AnnotatedString

}