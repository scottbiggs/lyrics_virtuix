package com.virtuix.lyricstats

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.virtuix.lyricstats.apis.dictionary.DictionaryApiClient
import com.virtuix.lyricstats.apis.dictionary.DictionaryApiInterface
import com.virtuix.lyricstats.apis.dictionary.DictionaryEntry
import com.virtuix.lyricstats.apis.lyric.LyricApiInterface
import com.virtuix.lyricstats.apis.lyric.LyricApiClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException


private const val TAG = "MainViewModel"

class MainViewModel(
	private val lyricApi: LyricApiInterface = LyricApiClient.lyricApi,
	private val dictApi: DictionaryApiInterface = DictionaryApiClient.dictionaryApi,
	ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), MainViewModelInterface {


	//------------------------------------------
	//	class variables
	//------------------------------------------

	private val ioScope = CoroutineScope(ioDispatcher)

	private val _uiState = MutableStateFlow(MainUiState())
	val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

	private val _errState = MutableStateFlow(ErrState(errType = ErrStateType.NONE, errState = false))
	val errState: StateFlow<ErrState> = _errState.asStateFlow()


	//------------------------------------------
	//	functions
	//------------------------------------------

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

		if (checkArtistAndTitleInput() == false) {
			return
		}

		ioScope.launch {
			signalBeginProcessing()

			val response = try {
				lyricApi.lyrics(
					artist = _uiState.value.artist,
					songTitle = _uiState.value.songTitle
				)
			}

			catch (e : Exception) {
				processLyricExceptions(e)
				return@launch
			}

			val lyrics = response.lyrics
			Log.v(TAG,"Raw lyrics: $lyrics")

			_uiState.update { it.copy(
				wordMap = getWordsFromString(lyrics),
				thinking = false
			) }
		} // ioScope.launch
	}


	/**
	 * Parse out the proper thing to do with the various exceptions
	 */
	private fun processLyricExceptions(e: Exception) {
		when (e) {
			is HttpException -> {
				lyricsHttpException(e)
			}

			is SocketTimeoutException -> {
				lyricsTimeoutException(e)
			}

			else -> {
				lyricsUnknownServerError(e)
			}
		}

		// finish processing and exit (after clearing any lingering definitions)
		_uiState.update { it.copy(thinking = false, definition = AnnotatedString("")) }
	}

	/**
	 * First part of looking up and processing lyrics: check input.
	 *
	 * @return		True means this part was successful.  Processing should continue.
	 * 				False indicates a problem and processing should stop.
	 */
	private fun checkArtistAndTitleInput() : Boolean {
		if (_uiState.value.artist.isBlank()) {
			errorNoArtist()
			return false
		}
		if (_uiState.value.songTitle.isBlank()) {
			errorNoTitle()
			return false
		}
		return true
	}

	private fun signalBeginProcessing() {
		_uiState.update { it.copy(
			thinking = true,
			currentWord = "",
			definition = AnnotatedString("")
		) }
	}

	private fun lyricsUnknownServerError(e: Exception) {
		Log.e(TAG, "Unknow server error\n${e.message}")
		_errState.update {
			it.copy(
				errType = ErrStateType.NONE,
				errState = true,
				errDescId = R.string.unknown_server_exception,
			)
		}
		_uiState.update {
			it.copy(currentWord = "",
				definition = AnnotatedString(""),
				wordMap = hashMapOf()
			)
		}
	}

	private fun lyricsTimeoutException(e: Exception) {
		Log.e(TAG, "Timeout Exception:\n ${e.message}")
		_errState.update {
			it.copy(
				errType = ErrStateType.NONE,
				errState = true,
				errMsgId = R.string.timeout
			)
		}
		_uiState.update {
			it.copy(currentWord = "",
				definition = AnnotatedString(""),
				wordMap = hashMapOf())
		}
	}

	private fun lyricsHttpException(e: Exception) {
		Log.e(TAG, "HttpException while trying to process lyrics:\n${e.message}")
		_errState.update {
			it.copy(
				errType = ErrStateType.ARTIST_AND_TITLE,
				errState = true,
				errMsgId = R.string.unable_to_find,
				artist = _uiState.value.artist,
				title = _uiState.value.songTitle
			)
		}
		_uiState.update {
			it.copy(currentWord = "",
				definition = AnnotatedString(""),
				wordMap = hashMapOf())
		}
	}

	private fun errorNoTitle() {
		_errState.update {
			it.copy(
				errType = ErrStateType.NONE,
				errState = true,
				errMsgId = R.string.invalid_title
			)
		}
		_uiState.update { it.copy(currentWord = "", definition = AnnotatedString(""), wordMap = hashMapOf()) }
	}

	private fun errorNoArtist() {
		_errState.update {
			it.copy(
				errType = ErrStateType.NONE,
				errState = true,
				errMsgId = R.string.invalid_artist
			)
		}
		_uiState.update { it.copy(currentWord = "", definition = AnnotatedString(""), wordMap = hashMapOf()) }
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


	/**
	 * Given a string that probably contains a bunch of words, extract
	 * all the words from it.
	 *
	 * @param	bigString		A string that probably has lots of words in it.
	 * 							Can be big.
	 *
	 * @return	A hashmap with all the unique strings and how many times each
	 * 			appears.
	 *
	 * NOTE
	 * 		Some things that we don't normally call words will be
	 * 		included in this list.  Numbers, strings of non-alphanumeric
	 * 		characters, and more will be considered a word by this
	 * 		function.
	 */
	private fun getWordsFromString(bigString : String) : Map<String, Int> {
		val wordList = bigString.split("\\s+".toRegex()).map { word ->
			word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
				.filter {
					it.isLetterOrDigit() or (it == '\'') or (it == '_')
				}.lowercase()
		}

		val wordHash = mutableMapOf<String, Int>()
		for (word in wordList) {
			if (wordHash.containsKey(word)) {
				wordHash.put(word, wordHash.getValue(word) + 1)
			}
			else {
				wordHash.put(word, 1)
			}
		}

		// space is not considered a word
		if (wordHash.containsKey("")) {
			wordHash.remove("")
		}

		// sort alphabetically and finish
		return wordHash.toMap().toSortedMap()
	}


	/**
	 * Finds the definition of a word.
	 *
	 * side effects
	 * 		uiState.definition		Updated to hold this new definition.
	 * 								This should cause a recomposition.
	 *
	 * 	NOTE
	 * 		This works in a coroutine off the main thread.
	 */
	override fun getDefinition(word : String) {

		ioScope.launch {
			val response = try {
				dictApi.dictLookup(word.lowercase())
			}

			catch (e : Exception) {
				dictionaryServerProblems()

				// finish processing and exit
				_uiState.update { it.copy(thinking = false) }
			} as Response<List<DictionaryEntry>>


			if (response.isSuccessful) {
				val responseList = response.body() as List<DictionaryEntry>
				Log.v(TAG, "dictionary -> $responseList")


				val fullDef = buildDefinition(word, responseList)
				_uiState.update { it.copy(definition = fullDef) }
			}
			else {
				noDictionaryEntry(word)
			}
		}
	}

	/**
	 * Updates the error state to indicate that there was some sort
	 * of server issue when looking up a word in its dictionary.
	 */
	private fun dictionaryServerProblems() {
		_errState.update {
			it.copy(
				errType = ErrStateType.NONE,
				errState = true,
				errMsgId = R.string.dictionary_server_problem,
			)
		}

	}

	/**
	 * Given a word and its complete definition (a list of [DictionaryEntry]s,
	 * this makes a pretty representation suitable for reading.
	 *
	 * Format/example:
	 *
	 *  duck
	 *     Entry 1
	 * 	     meaning 1: verb
	 * 		    definition 1 - To quickly lower head...
	 * 			definition 2 - To quickly lower (the head...
	 * 			...
	 * 		meaning 2: noun
	 * 			definition 1 - ...
	 * 	  Entry 2
	 * 		meaning 1: noun
	 * 			definition 1 - An aquatic bird...
	 * 			definition 2 - ...
	 *
	 * 	and so on
	 *
	 *
	 * @return		An [AnnotatedString] of the word and full definition.
	 */
	private fun buildDefinition(word: String, responseList: List<DictionaryEntry>) : AnnotatedString {

		return buildAnnotatedString {
			withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) {
				append("$word\n")
			}

			for ((i, entry) in responseList.withIndex()) {
				append("  Entry ")
				append("${i + 1}\n")

				for ((j, meaning) in entry.meanings.withIndex()) {
					append("    Meaning ")
					append("${j + 1}: ")
					withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
						append("${meaning.partOfSpeech}\n")
					}

					for ((k, def) in meaning.definitions.withIndex()) {
						append("      Def ")
						append("${k + 1} - ")
						withStyle(style = SpanStyle(fontFamily = FontFamily.Serif)) {
							append("${def.definition}\n")
						}
					}
				}
				append("\n")
			}
		}
	}
	/**
	 * Updates the error state when a word was selected but the dictionary
	 * API has no entry.  Can be surprising sometimes.
	 *
	 * side effects
	 * 		_errState
	 */
	private fun noDictionaryEntry(entry: String) {
		_errState.update {
			it.copy(
				errType = ErrStateType.WORD,
				errState = true,
				errMsgId = R.string.not_a_word,
				word = entry
			)
		}
	}
}
