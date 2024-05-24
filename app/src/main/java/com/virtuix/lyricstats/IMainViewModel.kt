package com.virtuix.lyricstats

interface IMainViewModel {
	fun updateArtist(artist: String)
	fun updateSongTitle(songTitle: String)
	fun lookUpAndProcessLyrics()
}