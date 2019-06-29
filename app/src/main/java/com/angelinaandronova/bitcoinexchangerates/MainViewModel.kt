package com.angelinaandronova.bitcoinexchangerates

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*


class MainViewModel : ViewModel(), CoroutineScope by MainScope() {

    val chartEntries: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
    var timespan: MutableLiveData<TimeSpan> = MutableLiveData<TimeSpan>().default(TimeSpan.WEEK)
    val state: Pair<MutableLiveData<TimeSpan>, MutableLiveData<ArrayList<Entry>>> = Pair(timespan, chartEntries)

    fun loadData(timespan: TimeSpan) {
        launch {
            val service = RetrofitClientInstance.getRetrofitInstance().create(BitcoinRatesService::class.java)
            val call = service.getRatesForChart(timespan = timespan.queryParam)

            val response = withContext(Dispatchers.IO) { call.execute() }
            Log.i("ANGELINA1234", "${response.body()}")

            val entries = arrayListOf<Entry>()
            response.body()?.values?.forEach { entries.add(Entry(it.x, it.y)) }
            chartEntries.value = entries
        }
    }

    enum class TimeSpan(val queryParam: String) {
        DAY("1days"),
        WEEK("1week"),
        YEAR("1year")
    }

}