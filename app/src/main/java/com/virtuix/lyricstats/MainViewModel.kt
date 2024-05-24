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

	private val _errSTate = MutableStateFlow(ErrState())
	val errState: StateFlow<ErrState> = _errSTate.asStateFlow()



	override fun updateArtist(artist: String) {
		_uiState.update { it.copy(artist = artist) }
	}

	override fun updateSongTitle(songTitle: String) {
		_uiState.update { it.copy(songTitle = songTitle) }
	}

	override fun lookUpAndProcessLyrics() {

		// first, check for valid input
		if (_uiState.value.artist.isBlank()) {
			_errSTate.update {
				it.copy(
					isErr = true,
					errMsgId = R.string.invalid_artist
				)
			}
			return
		}
		if (_uiState.value.songTitle.isBlank()) {
			_errSTate.update {
				it.copy(
					isErr = true,
					errMsgId = R.string.invalid_title
				)
			}
			return
		}

		ioScope.launch {
			val response = lyricApi.lyrics(
				artist = _uiState.value.artist,
				songTitle = _uiState.value.songTitle
			)
			val lyrics = response.lyrics
			Log.d(TAG,"Raw lyrics: $lyrics")
			findLongestWord(lyrics)
		}
	}

	private fun findLongestWord(lyrics: String) {
		val longestWord = lyrics.split("\\s+".toRegex()).reduce { longest, current ->
			if(current.length > longest.length) current else longest
		}
		_uiState.update { it.copy(longestWord = longestWord) }
	}

}
