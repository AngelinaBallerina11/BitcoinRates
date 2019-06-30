package com.angelinaandronova.bitcoinexchangerates.di.modules

import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainRepository
import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainRepositoryImpl
import com.angelinaandronova.bitcoinexchangerates.utils.SchedulerProvider
import com.angelinaandronova.bitcoinexchangerates.utils.SchedulerProviderImpl
import dagger.Binds
import dagger.Module


@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(repo: MainRepositoryImpl): MainRepository

    @Binds
    abstract fun bindScheduler(schedulerProvider: SchedulerProviderImpl): SchedulerProvider

}