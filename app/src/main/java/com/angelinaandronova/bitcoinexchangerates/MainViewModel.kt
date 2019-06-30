package com.angelinaandronova.bitcoinexchangerates

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*


class MainViewModel(val app: Application) : AndroidViewModel(app), CoroutineScope by MainScope() {

    val screenState = MutableLiveData<ScreenState>()

    init {
        tryToLoadData()
    }

    fun tryToLoadData() {
        if (noInternetConnection()) {
            screenState.value = ScreenState.NoConnection
        } else {
            screenState.value = ScreenState.Loading()
        }
    }

    private fun noInternetConnection(): Boolean =
        (app.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
            ?.activeNetworkInfo?.isConnected != true

    fun loadData(timespan: TimeSpan) {
        if (noInternetConnection()) {
            screenState.value = ScreenState.NoConnection
            return
        }
        launch {
            val service = RetrofitClientInstance.getRetrofitInstance().create(BitcoinRatesService::class.java)
            val call = service.getRatesForChart(timespan = timespan.queryParam)

            val response = withContext(Dispatchers.IO) { call.execute() }
            Log.i("ANGELINA1234", "${response.body()}")

            val entries = arrayListOf<Entry>()
            response.body()?.values?.forEach { entries.add(Entry(it.x, it.y)) }
            screenState.value = ScreenState.DisplayData(Pair(timespan, entries))
        }
    }

    enum class TimeSpan(val queryParam: String) {
        DAY("1days"),
        WEEK("1week"),
        YEAR("1year")
    }

    sealed class ScreenState {
        object NoConnection : ScreenState()
        data class Loading(val timespan: TimeSpan = TimeSpan.WEEK) : ScreenState()
        data class DisplayData(val chartEntries: Pair<TimeSpan, ArrayList<Entry>>) : ScreenState()
        // TODO: error state
    }
}