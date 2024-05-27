package com.virtuix.lyricstats

import android.util.Log
import androidx.lifecycle.ViewModel
import com.virtuix.lyricstats.ui.ErrState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
			catch (e : HttpException) {
				_errState.update {
					it.copy(
						errState = true,
						errMsgId = R.string.unable_to_find,
						artist = _uiState.value.artist,
						title = _uiState.value.songTitle
					)
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
		_uiState.update { it.copy(longestWord = longestWord) }
	}

}
