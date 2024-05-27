package com.virtuix.lyricstats

import retrofit2.http.GET
import retrofit2.http.Path

interface LyricApi {

	/**
	 * GETs lyrics for the given artist and song.
	 */
	@GET("{artist}/{songTitle}")
	suspend fun lyrics(@Path("artist") artist: String, @Path("songTitle") songTitle: String): LyricResponse
}
