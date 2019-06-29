package com.angelinaandronova.bitcoinexchangerates

import com.angelinaandronova.bitcoinexchangerates.model.BitcoinRatesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BitcoinRatesService {

    @GET(TRANSACTIONS_PER_SECOND_PATH)
    fun getRatesForChart(
        @Query(FORMAT) format: String = DEFAULT_FORMAT,
        @Query(TIMESPAN) timespan: String = DEFAULT_TIMESPAN,
        @Query(ROLLING_AVERAGE) rollingAverage: String = DEFAULT_ROLLING_AVERAGE
    ): Call<BitcoinRatesResponse>

    companion object {
        const val FORMAT = "format"
        const val DEFAULT_FORMAT = "json"
        const val TIMESPAN = "timespan"
        const val DEFAULT_TIMESPAN = "1weeks"
        const val ROLLING_AVERAGE = "rollingAverage"
        const val DEFAULT_ROLLING_AVERAGE = "8hours"
        const val TRANSACTIONS_PER_SECOND_PATH = "/charts/transactions-per-second"
    }
}