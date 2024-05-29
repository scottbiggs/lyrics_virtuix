package com.virtuix.lyricstats.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
	private const val LYRICS_BASE_URL = "https://api.lyrics.ovh/v1/"

	// todo: localization
//	private const val DICT_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/"
	private const val DICT_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"

	/**
	 * Accesses the lyrics server
	 */
	val lyricApi: Retrofit by lazy {
		Retrofit.Builder()
			.baseUrl(LYRICS_BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	/**
	 * Accesses the dictionary server
	 */
	val dictApi: Retrofit by lazy {
		Retrofit.Builder()
			.baseUrl(DICT_BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

}