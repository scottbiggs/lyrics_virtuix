package com.virtuix.lyricstats

object LyricApiClient {
	val lyricApi: LyricApi by lazy {
		RetrofitClient.retrofit.create(LyricApi::class.java)
	}
}