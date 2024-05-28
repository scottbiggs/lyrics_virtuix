package com.virtuix.lyricstats.apis.lyric

import com.virtuix.lyricstats.apis.RetrofitClient

object LyricApiClient {
	val lyricApi: LyricApiInterface by lazy {
		RetrofitClient.lyricApi.create(LyricApiInterface::class.java)
	}
}