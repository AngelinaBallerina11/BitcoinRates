package com.angelinaandronova.bitcoinexchangerates.nework

import com.angelinaandronova.bitcoinexchangerates.nework.model.BitcoinRatesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BitcoinRatesService {

    @GET(MARKET_PRICE)
    fun getRatesForChart(
        @Query(FORMAT) format: String = DEFAULT_FORMAT,
        @Query(TIMESPAN) timespan: String = DEFAULT_TIMESPAN,
        @Query(ROLLING_AVERAGE) rollingAverage: String = DEFAULT_ROLLING_AVERAGE
    ): Single<BitcoinRatesResponse>

    companion object {
        const val FORMAT = "format"
        const val DEFAULT_FORMAT = "json"
        const val TIMESPAN = "timespan"
        const val DEFAULT_TIMESPAN = "1weeks"
        const val ROLLING_AVERAGE = "rollingAverage"
        const val DEFAULT_ROLLING_AVERAGE = "8hours"
        const val MARKET_PRICE = "/charts/market-price"
    }
}