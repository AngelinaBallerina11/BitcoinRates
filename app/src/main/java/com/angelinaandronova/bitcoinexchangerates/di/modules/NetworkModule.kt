package com.angelinaandronova.bitcoinexchangerates.di.modules

import com.angelinaandronova.bitcoinexchangerates.nework.BitcoinRatesService
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


/**
 * Module which provides all required dependencies for network
 */
@Module
class NetworkModule {

    private val baseUrl = "https://api.blockchain.info"

    /**
     * Provides the Bitcoin rates service implementation.
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the Bitcoin GET rates service implementation.
     */
    @Provides
    @Singleton
    fun provideBitcoinRatesApi(retrofit: Retrofit): BitcoinRatesService {
        return retrofit.create(BitcoinRatesService::class.java)
    }

    /**
     * Provides the Retrofit object.
     * @return the Retrofit object
     */
    @Provides
    @Singleton
    fun provideRetrofitInterface(): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()
}