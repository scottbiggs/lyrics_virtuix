package com.virtuix.lyricstats

interface IMainViewModel {
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
	fun processError(wasHandled: Boolean = true)
}