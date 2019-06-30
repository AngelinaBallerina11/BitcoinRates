package com.angelinaandronova.bitcoinexchangerates.mainScreen

import com.angelinaandronova.bitcoinexchangerates.nework.BitcoinRatesService
import com.angelinaandronova.bitcoinexchangerates.nework.model.BitcoinRatesResponse
import com.angelinaandronova.bitcoinexchangerates.utils.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(
    private val service: BitcoinRatesService,
    private val scheduler: SchedulerProvider
) :
    MainRepository {

    override fun retrieveData(timeSpan: MainViewModel.TimeSpan): Single<BitcoinRatesResponse> =
        service.getRatesForChart(timespan = timeSpan.queryParam)
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

}

interface MainRepository {
    fun retrieveData(timeSpan: MainViewModel.TimeSpan): Single<BitcoinRatesResponse>
}