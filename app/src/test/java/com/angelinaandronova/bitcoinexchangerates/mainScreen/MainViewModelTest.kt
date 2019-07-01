package com.angelinaandronova.bitcoinexchangerates.mainScreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.angelinaandronova.bitcoinexchangerates.TestUtils
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val repository = mock<MainRepository>()
    private val connection = mock<Connection>()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(connection, repository)
    }

    @Test
    fun `tryToLoadData when offline`() {
        whenever(connection.isOffline()).thenReturn(true)
        viewModel.tryToLoadData()
        assert(viewModel.screenState.value is MainViewModel.ScreenState.NoConnection)
    }

    @Test
    fun `tryToLoadData when online`() {
        whenever(connection.isOffline()).thenReturn(false)
        viewModel.tryToLoadData()
        assert(viewModel.screenState.value is MainViewModel.ScreenState.Loading)
    }

    @Test
    fun `loadData when offline`() {
        whenever(connection.isOffline()).thenReturn(true)
        viewModel.loadData(TestUtils.getRandomTimeSpan())
        assert(viewModel.screenState.value is MainViewModel.ScreenState.NoConnection)
    }

    @Test
    fun `loadData when online`() {
        val timeSpan = MainViewModel.TimeSpan.MONTH
        whenever(connection.isOffline()).thenReturn(false)
        whenever(repository.retrieveData(timeSpan)).thenReturn(
            Single.just(
                TestUtils.getDefaultSuccessfulResponse(
                    1
                )
            )
        )
        viewModel.loadData(timeSpan)
        assert(viewModel.screenState.value is MainViewModel.ScreenState.DisplayData)
    }
}