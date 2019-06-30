package com.angelinaandronova.bitcoinexchangerates.di

import com.angelinaandronova.bitcoinexchangerates.BitcoinApp
import com.angelinaandronova.bitcoinexchangerates.di.modules.AppModule
import com.angelinaandronova.bitcoinexchangerates.di.modules.NetworkModule
import com.angelinaandronova.bitcoinexchangerates.di.modules.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityBuilder::class,
        NetworkModule::class,
        ViewModelModule::class,
        RepositoryModule::class]
)
interface AppComponent : AndroidInjector<BitcoinApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: BitcoinApp): Builder

        fun build(): AppComponent
    }

}