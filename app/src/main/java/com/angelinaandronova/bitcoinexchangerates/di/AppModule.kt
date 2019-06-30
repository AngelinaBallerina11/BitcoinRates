package com.angelinaandronova.bitcoinexchangerates.di

import android.content.Context
import com.angelinaandronova.bitcoinexchangerates.BitcoinApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: BitcoinApp): Context = application

}