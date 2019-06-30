package com.angelinaandronova.bitcoinexchangerates.mainScreen

import com.angelinaandronova.bitcoinexchangerates.TestUtils
import com.angelinaandronova.bitcoinexchangerates.TrampolineSchedulerProvider
import com.angelinaandronova.bitcoinexchangerates.nework.BitcoinRatesService
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainRepositoryImplTest {

    private val goodResponse = TestUtils.getDefaultSuccessfulResponse(2)
    private var schedulerProvider = TrampolineSchedulerProvider()
    private var service = mock<BitcoinRatesService> { on { getRatesForChart() } doReturn Single.just(goodResponse) }
    private lateinit var repositoryImpl: MainRepositoryImpl

    @Before
    fun setUp() {
        repositoryImpl = MainRepositoryImpl(service, schedulerProvider)
    }

    @Test
    fun `when retrieveData is called service calls getRatesForChart exactly once`() {
        repositoryImpl.retrieveData(MainViewModel.TimeSpan.WEEK)
        verify(service, times(1)).getRatesForChart()
    }

    @Test
    fun `repository retrieveData returns BitcoinRatesResponse observable`() {
        repositoryImpl.retrieveData(MainViewModel.TimeSpan.WEEK).test().assertValue(goodResponse)
    }
}