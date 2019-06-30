package com.angelinaandronova.bitcoinexchangerates

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import javax.inject.Inject


class MainViewModel @Inject constructor(private val appContext: Context, val repo: MainRepository) : ViewModel() {

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
        (appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
            ?.activeNetworkInfo?.isConnected != true

    fun loadData(timespan: TimeSpan) {
        if (noInternetConnection()) {
            screenState.value = ScreenState.NoConnection
            return
        }

        /* val service = RetrofitClientInstance.getRetrofitInstance().create(BitcoinRatesService::class.java)
         val call = service.getRatesForChart(timespan = timespan.queryParam)

         val response = withContext(Dispatchers.IO) { call.execute() }
         Log.i("ANGELINA1234", "${response.body()}")

         val entries = arrayListOf<Entry>()
         response.body()?.values?.forEach { entries.add(Entry(it.x, it.y)) }
         screenState.value = ScreenState.DisplayData(Pair(timespan, entries))*/

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