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

	// todo: use this!!
	private val _currentWord = MutableStateFlow("")
	val currentWord: StateFlow<String> = _currentWord

	// todo: implement this!
	private val _currentDefinition = MutableStateFlow("")
	val currentDefinition: StateFlow<String> = _currentDefinition



	override fun updateArtist(artist: String) {
		_uiState.update { it.copy(artist = artist) }
	}

	override fun updateSongTitle(songTitle: String) {
		_uiState.update { it.copy(songTitle = songTitle) }
	}

	override fun lookUpAndProcessLyrics() {

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
				return@launch
			}

			val lyrics = response.lyrics
			Log.d(TAG,"Raw lyrics: $lyrics")
			findLongestWord(lyrics)
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
		_uiState.update { it.copy(currentWord = longestWord) }
	}


	/**
	 * Goes through the given string and figures out which word
	 * is most commonly used.
	 *
	 * side effect:
	 * 		currentWord		Will hold the most commonly used
	 * 						word in the given lyrics.
	 */
	private fun findMostCommonWord(lyrics: String) {
		// todo
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

}