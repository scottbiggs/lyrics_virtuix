package com.virtuix.lyricstats.apis.lyric

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the interface for the api to get lyrics.
 */
interface LyricApiInterface {

	/**
	 * GETs lyrics for the given artist and song.
	 */
	@GET("{artist}/{songTitle}")
	suspend fun lyrics(@Path("artist") artist: String, @Path("songTitle") songTitle: String): LyricResponse
}
