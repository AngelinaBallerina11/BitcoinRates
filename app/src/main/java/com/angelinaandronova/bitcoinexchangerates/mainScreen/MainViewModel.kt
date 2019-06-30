package com.angelinaandronova.bitcoinexchangerates.mainScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angelinaandronova.bitcoinexchangerates.nework.model.BitcoinRatesResponse
import com.github.mikephil.charting.data.Entry
import io.reactivex.disposables.Disposable
import java.io.IOException
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val connection: Connection,
    private val repo: MainRepository
) : ViewModel() {

    val screenState = MutableLiveData<ScreenState>()
    private lateinit var disposable: Disposable

    init {
        tryToLoadData()
    }

    fun tryToLoadData() {
        if (connection.isOffline()) {
            screenState.value = ScreenState.NoConnection
        } else {
            screenState.value = ScreenState.Loading()
        }
    }

    fun loadData(timespan: TimeSpan) {
        if (connection.isOffline()) {
            screenState.value =
                ScreenState.NoConnection
            return
        }

        disposable = repo.retrieveData(timespan)
            .subscribe(
                { response -> displayEntries(response, timespan) },
                ::handleError
            )

    }

    private fun displayEntries(response: BitcoinRatesResponse, timespan: TimeSpan) {
        val entries = arrayListOf<Entry>()
        response.values.forEach { entries.add(Entry(it.x, it.y)) }
        screenState.value = ScreenState.DisplayData(
            Pair(
                timespan,
                entries
            )
        )
    }

    private fun handleError(throwable: Throwable) {
        screenState.value =
            ScreenState.NetworkError(throwable.localizedMessage)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    enum class TimeSpan(val queryParam: String) {
        DAY("1days"),
        WEEK("1weeks"),
        YEAR("1years")
    }

    sealed class ScreenState {
        object NoConnection : ScreenState()
        data class Loading(val timespan: TimeSpan = TimeSpan.WEEK) : ScreenState()
        data class DisplayData(val chartEntries: Pair<TimeSpan, ArrayList<Entry>>) : ScreenState()
        data class NetworkError(val message: String) : ScreenState()
    }
}

class Connection @Inject constructor() {
    fun isOffline(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
            return ipProcess.waitFor() != 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return true
    }
}