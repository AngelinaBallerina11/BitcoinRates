package com.angelinaandronova.bitcoinexchangerates.di

import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}