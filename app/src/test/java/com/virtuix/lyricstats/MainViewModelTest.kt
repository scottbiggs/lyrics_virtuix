package com.virtuix.lyricstats

import com.virtuix.lyricstats.apis.lyric.LyricApiInterface
import com.virtuix.lyricstats.apis.lyric.LyricResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

	companion object {
		private const val ARTIST = "Nine Inch Nails"
		private const val SONG_TITLE = "The Only Time"
		private const val LYRICS = """
Paroles de la chanson THE ONLY TIME par Nine Inch Nails
I'm drunk
And right now i'm so in love with you
And i don't want to think too much about what we should or shouldn't do
Lay my hands on Heaven and the sun and the moon and the stars
While the devil wants to fuck me in the back of his car

Nothing quite like the feel of something new
Maybe i'm all messed up
Maybe i'm all messed up
Maybe i'm all messed up in you
Maybe i'm all messed up
Maybe i'm all messed up
Maybe i'm all messed up

Maybe i'm all messed up in you
Maybe i'm all messed up
This is the only time i really feel alive
This is the only time i really feel alive

I swear
I just found everything i need
The sweat in your eyes the blood in your veins are listening to me
Well i want to drink it up and swim in it until i drown
My moral standing is lying down

Nothing quite like the feel of something new
Maybe i'm all messed up
Maybe i'm all messed up
Maybe i'm all messed up in you
Maybe i'm all messed up

Maybe i'm all messed up
Maybe i'm all messed up
Maybe i'm all messed up in you
Maybe i'm all messed up
This is the only time i really feel alive
This is the only time i really feel alive
"""
	}

	private val lyricApp = mock<LyricAppInterface>()
	private val lyricApi = mock<LyricApiInterface>()
	private val response = LyricResponse(lyrics = LYRICS)
	private val subject = MainViewModel(lyricApi = lyricApi)

	@Before
	fun setup() {
		lyricApi.stub {
			onBlocking { lyrics(artist = ARTIST, songTitle = SONG_TITLE) }.doReturn(response)
		}
	}

	@Test
	fun `updateArtist() updates the artist`() {
		subject.updateArtist(artist = ARTIST)

		assertEquals(ARTIST, subject.uiState.value.artist)
	}

	@Test
	fun `updateSongTitle() updates the song title`() {
		subject.updateSongTitle(songTitle = SONG_TITLE)

		assertEquals(SONG_TITLE, subject.uiState.value.songTitle)
	}

	@Test
	fun `lookUpAndProcessLyrics() requests the lyrics for the author and the song title`() = runTest {
		val subject = MainViewModel(lyricApi = lyricApi, ioDispatcher = StandardTestDispatcher(testScheduler))
		subject.updateArtist(artist = ARTIST)
		subject.updateSongTitle(songTitle = SONG_TITLE)

		subject.lookUpAndProcessLyrics()
		advanceUntilIdle()

		verify(lyricApi).lyrics(artist = ARTIST, songTitle = SONG_TITLE)
	}

	@Test
	fun `lookUpAndProcessLyrics() processes the lyrics and find the longest word`() = runTest {
		val subject = MainViewModel(lyricApi = lyricApi, ioDispatcher = StandardTestDispatcher(testScheduler))
		subject.updateArtist(artist = ARTIST)
		subject.updateSongTitle(songTitle = SONG_TITLE)

		subject.lookUpAndProcessLyrics()
		advanceUntilIdle()

		assertEquals("everything", subject.uiState.value.currentWord)
	}
}