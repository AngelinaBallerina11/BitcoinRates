package com.angelinaandronova.bitcoinexchangerates.di

import com.angelinaandronova.bitcoinexchangerates.MainRepository
import com.angelinaandronova.bitcoinexchangerates.MainRepositoryImpl
import dagger.Binds
import dagger.Module


@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(repo: MainRepositoryImpl): MainRepository

}