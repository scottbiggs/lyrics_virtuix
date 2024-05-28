package com.virtuix.lyricstats

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MainViewModel(
	private val lyricApi: LyricApi = LyricApiClient.lyricApi,
	ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel(), IMainViewModel {

	companion object {
		private const val TAG = "MainViewModel"
	}

	private val ioScope = CoroutineScope(ioDispatcher)
	private val _uiState = MutableStateFlow(MainUiState())
	val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

	private val _errState = MutableStateFlow(ErrState(errState = false))
	val errState: StateFlow<ErrState> = _errState.asStateFlow()



	override fun updateArtist(artist: String) {
		_uiState.update { it.copy(artist = artist) }
	}

	override fun updateSongTitle(songTitle: String) {
		_uiState.update { it.copy(songTitle = songTitle) }
	}

	/**
	 * When the processing changes, let the viewmodel know here.
	 *
	 * @param	choice		True -> process to find the most common word
	 * 						False -> process to find the longest word (default)
	 */
	override fun updateProcessChoice(choice: Boolean) {
		_uiState.update { it.copy(processChoice = choice) }
		lookUpAndProcessLyrics()
	}

	override fun lookUpAndProcessLyrics() {

		// todo:  check to see if anything has changed since the last api call

		// first, check for valid input
		if (_uiState.value.artist.isBlank()) {
			_errState.update {
				it.copy(
					errState = true,
					errMsgId = R.string.invalid_artist
				)
			}
			return
		}
		if (_uiState.value.songTitle.isBlank()) {
			_errState.update {
				it.copy(
					errState = true,
					errMsgId = R.string.invalid_title
				)
			}
			return
		}

		ioScope.launch {

			// signal that processing has begun
			_uiState.update { it.copy(thinking = true) }

			val response = try {
				lyricApi.lyrics(
					artist = _uiState.value.artist,
					songTitle = _uiState.value.songTitle
				)
			}

			catch (e : Exception) {
				when (e) {
					is HttpException -> {
						Log.e(TAG, "HttpException while trying to process lyrics:\n${e.message}")
						_errState.update {
							it.copy(
								errState = true,
								errMsgId = R.string.unable_to_find,
								artist = _uiState.value.artist,
								title = _uiState.value.songTitle
							)
						}
					}

					is SocketTimeoutException -> {
						Log.e(TAG, "Timeout Exception:\n ${e.message}")
						_errState.update {
							it.copy(
								errState = true,
								errMsgId = R.string.timeout
							)
						}
					}
					else -> {
						Log.e(TAG, "Unknow server error")
						_errState.update {
							it.copy(
								errState = true,
								errDescId = R.string.unknown_server_exception
							)
						}
					}
				}

				// finish processing and exit
				_uiState.update { it.copy(thinking = false) }
				return@launch
			}

			val lyrics = response.lyrics
			Log.d(TAG,"Raw lyrics: $lyrics")

			// finally process the lyrics!
			if (_uiState.value.processChoice) {
				findMostCommonWord(lyrics)
			}
			else {
				findLongestWord(lyrics)
			}

			// processing has finally finished
			_uiState.update { it.copy(thinking = false) }
		}
	}


	/**
	 * Call this after an error has been signaled.  This can be
	 * used to reset the error condition.
	 *
	 * @param	wasErrorHandled		Was the error handled? then set this
	 * 								to true.  If the error should persist,
	 * 								this should be false. Defaults to true.
	 */
	override fun processError(wasErrorHandled : Boolean) {
		_errState.update { it.copy(errState = !wasErrorHandled) }
	}


	private fun findLongestWord(lyrics: String) {
		val longestWord = lyrics.split("\\s+".toRegex()).reduce { longest, current ->
			if(current.length > longest.length) current else longest
		}
		Log.i(TAG, "findLongestWord() -> $longestWord")
		_uiState.update { it.copy(currentWord = longestWord) }
	}


	/**
	 * Goes through the given string and figures out which word
	 * is most commonly used.
	 *
	 * side effect:
	 * 		_uiState.currentWord	Will hold the most commonly used
	 * 								word in the given lyrics.
	 */
	private fun findMostCommonWord(lyrics: String) {

		//
		// method:
		// 		- create a list of all the words
		//		- turn the list into a hashmap where the
		//		  value is the number of times a word appears
		//		- find the word (which is the key) with the greatest value
		//
		//	O(n) speed (unless the hash function is really bad, which'll make it O(n^2))
		//	O(n) size

		val wordList = lyrics.split("\\s+".toRegex())

		val hashMap = mutableMapOf<String, Int>()
		for (word in wordList) {
			if (hashMap.containsKey(word)) {
				hashMap.put(word, hashMap.getValue(word) + 1)
			}
			else {
				hashMap.put(word, 1)
			}
		}

		var mostCommonWord = ""
		var mostCommonWordCount = 0

		for ((key, value) in hashMap) {
			if (value > mostCommonWordCount) {
				mostCommonWord = key
				mostCommonWordCount = value
			}
		}

		// save this (side effect!) in our flow
		Log.i(TAG, "findMostCommonWord() -> $mostCommonWord")
		_uiState.update { it.copy(currentWord = mostCommonWord) }
	}

}


/**
 * Stubb that is used for preview in [MainScreen].  Has no functionality
 */
class MainViewModelPreview() : IMainViewModel {

	val uiState = MainUiState()
	val errState = ErrState()


	override fun updateArtist(artist: String) {
		TODO("Not yet implemented")
	}

	override fun updateSongTitle(songTitle: String) {
		TODO("Not yet implemented")
	}

	override fun lookUpAndProcessLyrics() {
		TODO("Not yet implemented")
	}

	override fun processError(wasHandled: Boolean) {
		TODO("Not yet implemented")
	}

	override fun updateProcessChoice(choice: Boolean) {
		TODO("Not yet implemented")
	}
}