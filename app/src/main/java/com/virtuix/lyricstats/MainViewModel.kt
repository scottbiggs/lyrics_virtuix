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
	private val lyricApp: LyricApp = LyricApp.instance,
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

	/** the word to display */
	private var currentWord = ""

	/** able to determine if a word is an article, preposition, or interjection */
	private val wordFilter : FilteredWords by lazy { FilteredWords(lyricApp.getLyricAppContext()) }


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

		// first, check for valid input
		if (_uiState.value.artist.isBlank()) {
			_errState.update {
				it.copy(
					errType = ErrStateType.NONE,
					errState = true,
					errMsgId = R.string.invalid_artist
				)
			}
			return
		}
		if (_uiState.value.songTitle.isBlank()) {
			_errState.update {
				it.copy(
					errType = ErrStateType.NONE,
					errState = true,
					errMsgId = R.string.invalid_title
				)
			}
			return
		}

		ioScope.launch {

			// signal that processing has begun
			_uiState.update { it.copy(
				thinking = true,
				currentWord = "",
				definition = AnnotatedString("")
			) }

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
								errType = ErrStateType.ARTIST_AND_TITLE,
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
								errType = ErrStateType.NONE,
								errState = true,
								errMsgId = R.string.timeout
							)
						}
					}
					else -> {
						Log.e(TAG, "Unknow server error")
						_errState.update {
							it.copy(
								errType = ErrStateType.NONE,
								errState = true,
								errDescId = R.string.unknown_server_exception
							)
						}
					}
				}

				// finish processing and exit (after clearing any lingering definitions)
				_uiState.update { it.copy(thinking = false, definition = AnnotatedString("")) }
				return@launch
			}

			val lyrics = response.lyrics
			Log.d(TAG,"Raw lyrics: $lyrics")

			_uiState.update { it.copy(
				wordList = getWordsFromString(lyrics),
				thinking = false
			) }

//			// finally process the lyrics!
//			if (_uiState.value.processChoice) {
//				findMostCommonWord(lyrics)
//			}
//			else {
//				findLongestWord(lyrics)
//			}

//			// processing has finally finished
//			_uiState.update { it.copy(thinking = false) }
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


	/**
	 * Given a string that probably contains a bunch of words, extract
	 * all the words from it.
	 *
	 * @param	bigString		A string that probably has lots of words in it.
	 * 							Can be big.
	 *
	 * @param	filter			When true, filter out prepositions, articles,
	 * 							and interjections.  Defaults to false.
	 *
	 * NOTE
	 * 		Some things that we don't normally call words will be
	 * 		included in this list.  Numbers, strings of non-alphanumeric
	 * 		characters, and more will be considered a word by this
	 * 		function.
	 */
	private suspend fun getWordsFromString(bigString : String, filter : Boolean = false) : Set<String> {
		val wordList = bigString.split("\\s+".toRegex()).map { word ->
			word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
		}

		// convert to set, eliminating repeated words
		val wordSet = wordList.toMutableSet()

		if (filter) {
			for (word in wordSet) {
				if (wordFilter.shouldBeFiltered(word)) {
					wordSet.remove(word)
				}
			}
		}

		return wordSet
	}


	/**
	 * Only called within the coroutine that is looking up & processing the word.
	 */
//	private suspend fun findLongestWord(lyrics: String) {
//
//		val longestWord = getWordsFromString(lyrics).reduce { longest, current ->
//			if ((current.length > longest.length) and
//				(current.contains("---") == false) and
//				(wordFilter.unfiltered(current)))
//				current
//			else
//				longest
//		}
//
//		Log.i(TAG, "findLongestWord() -> $longestWord")
//
//		currentWord = longestWord
//		_uiState.update { it.copy(definition = getDefinition(longestWord)) }
//	}


	/**
	 * Goes through the given string and figures out which word
	 * is most commonly used.
	 *
	 * Only called within the coroutine that is looking up & processing the word.
	 *
	 * side effect:
	 * 		_uiState.currentWord	Will hold the most commonly used
	 * 								word in the given lyrics.
	 */
//	private fun findMostCommonWord(lyrics: String) {
//
//		//
//		// method:
//		// 		- create a list of all the words
//		//		- turn the list into a hashmap where the
//		//		  value is the number of times a word appears
//		//		- find the word (which is the key) with the greatest value
//		//
//		//	O(n) speed (unless the hash function is really bad, which'll make it O(n^2))
//		//	O(n) size
//
//		// grab all the words and filter out the articles, preps, and interjections.
//		val wordList = getWordsFromString(lyrics).filter { word ->
//			wordFilter.unfiltered(word)
//		}
//
//		val hashMap = mutableMapOf<String, Int>()
//		for (word in wordList) {
//			if (hashMap.containsKey(word)) {
//				hashMap.put(word, hashMap.getValue(word) + 1)
//			}
//			else {
//				hashMap.put(word, 1)
//			}
//		}
//
//		var mostCommonWord = ""
//		var mostCommonWordCount = 0
//
//		for ((key, value) in hashMap) {
//			if (value > mostCommonWordCount) {
//				mostCommonWord = key
//				mostCommonWordCount = value
//			}
//		}
//
//		// save this (side effect!) in our flow
//		Log.i(TAG, "findMostCommonWord() -> $mostCommonWord")
//		currentWord = mostCommonWord
//		_uiState.update { it.copy(definition = getDefinition(mostCommonWord)) }
//	}


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

		Log.d(TAG, "begin getDefinition($word)")

		ioScope.launch {
			val response = try {
				dictApi.dictLookup(word.lowercase())
			}

			catch (e : Exception) {
				_errState.update {
					it.copy(
						errType = ErrStateType.NONE,
						errState = true,
						errMsgId = R.string.dictionary_server_problem,
					)
				}

				// finish processing and exit
				_uiState.update { it.copy(thinking = false) }
			} as Response<List<DictionaryEntry>>


			if (response.isSuccessful) {
				val responseList = response.body() as List<DictionaryEntry>
				Log.v(TAG, "dictionary -> ${responseList}")

				//
				// Create output for all definitions.  Eg "duck"
				//
				// duck
				//    Entry 1
				//	     meaning 1: verb
				//		    definition 1 - To quickly lower head...
				//			definition 2 - To quickly lower (the head...
				//			...
				//		meaning 2: noun
				//			definition 1 - ...
				//	  Entry 2
				//		meaning 1: noun
				//			definition 1 - An aquatic bird...
				//			definition 2 - ...
				//
				//	and so on
				//

				val fullDef = buildAnnotatedString {
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
				_uiState.update { it.copy(definition = fullDef) }
			}
			else {
				// Could not find in dictionary api
				_errState.update {
					it.copy(
						errType = ErrStateType.WORD,
						errState = true,
						errMsgId = R.string.not_a_word,
						word = word
					)
				}
			}
		}
	}

}
