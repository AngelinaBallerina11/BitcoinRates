package com.angelinaandronova.bitcoinexchangerates

import com.angelinaandronova.bitcoinexchangerates.model.BitcoinRatesResponse
import com.angelinaandronova.bitcoinexchangerates.nework.BitcoinRatesService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(val service: BitcoinRatesService) : MainRepository {

    override fun retrieveData(timeSpan: MainViewModel.TimeSpan): Single<BitcoinRatesResponse> =
        service.getRatesForChart(timespan = timeSpan.queryParam)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}

interface MainRepository {
    fun retrieveData(timeSpan: MainViewModel.TimeSpan): Single<BitcoinRatesResponse>
}