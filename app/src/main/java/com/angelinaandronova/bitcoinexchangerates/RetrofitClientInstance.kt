package com.angelinaandronova.bitcoinexchangerates

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClientInstance {

    private var retrofit: Retrofit? = null

    private const val baseUrl = "https://api.blockchain.info"

    fun getRetrofitInstance(): Retrofit =
        retrofit ?: retrofit2.Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}