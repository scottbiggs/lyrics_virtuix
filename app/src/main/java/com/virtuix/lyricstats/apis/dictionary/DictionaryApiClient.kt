package com.virtuix.lyricstats.apis.dictionary

import com.virtuix.lyricstats.apis.RetrofitClient


object DictionaryApiClient {
    val dictionaryApi: DictionaryApiInterface by lazy {
        RetrofitClient.dictApi.create(DictionaryApiInterface::class.java)
    }
}