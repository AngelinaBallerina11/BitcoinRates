package com.angelinaandronova.bitcoinexchangerates.mainScreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.angelinaandronova.bitcoinexchangerates.test
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
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
    lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(connection, repository)
        whenever(connection.isOffline()).thenReturn(true)
    }

    @Test
    fun getScreenState() {
    }

    @Test
    fun tryToLoadData() {
        viewModel.tryToLoadData()
        viewModel.screenState.test().assertValueCount(1)
    }

    @Test
    fun loadData() {
    }
}