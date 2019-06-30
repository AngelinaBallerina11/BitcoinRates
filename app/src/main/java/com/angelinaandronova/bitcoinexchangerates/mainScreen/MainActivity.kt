package com.angelinaandronova.bitcoinexchangerates.mainScreen

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.angelinaandronova.bitcoinexchangerates.*
import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainViewModel.ScreenState.*
import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainViewModel.TimeSpan.*
import com.angelinaandronova.bitcoinexchangerates.chartUi.BitcoinMarkerView
import com.angelinaandronova.bitcoinexchangerates.di.ViewModelFactory
import com.angelinaandronova.bitcoinexchangerates.utils.getColorFromAttr
import com.angelinaandronova.bitcoinexchangerates.utils.hide
import com.angelinaandronova.bitcoinexchangerates.utils.show
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        pushProgress()

        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        mainViewModel.screenState.observe(this, Observer {
            when (it) {
                is NoConnection -> {
                    displayNoConnectionMessage()
                    Log.i("ANGELINA1234", "no connection")
                }
                is Loading -> {
                    pushProgress()
                    mainViewModel.loadData(it.timespan)
                    Log.i("ANGELINA1234", "loading")
                }
                is DisplayData -> {
                    displayMainContent()
                    Log.i("ANGELINA1234", "display data")
                    setUpChart(it.chartEntries)
                    setUpButtons(it.chartEntries.first)
                }
                is NetworkError -> showToast(it.message)
            }
        })

        btDay.setOnClickListener { mainViewModel.screenState.value = Loading(DAY) }
        btWeek.setOnClickListener { mainViewModel.screenState.value = Loading(WEEK) }
        btYear.setOnClickListener { mainViewModel.screenState.value = Loading(YEAR) }
        btRetry.setOnClickListener { mainViewModel.tryToLoadData() }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setUpButtons(timeSpan: MainViewModel.TimeSpan) {
        when (timeSpan) {
            DAY -> {
                btDay.select()
                btWeek.unselect()
                btYear.unselect()
            }
            WEEK -> {
                btWeek.select()
                btDay.unselect()
                btYear.unselect()
            }
            YEAR -> {
                btYear.select()
                btWeek.unselect()
                btDay.unselect()
            }
        }
    }

    private fun setUpChart(chartEntries: Pair<MainViewModel.TimeSpan, ArrayList<Entry>>) {
        val dataSet = LineDataSet(chartEntries.second, CHART_LABEL).apply {
            color = getColorFromAttr(R.attr.chartLineColor)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        val xAxisFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String =
                when (chartEntries.first) {
                    DAY -> formatForDay(value)
                    WEEK -> formatForWeek(value)
                    YEAR -> formatForYear(value)
                }

        }

        with(lineChart) {
            data = LineData(dataSet)
            xAxis.run {
                valueFormatter = xAxisFormatter
                granularity = when (chartEntries.first) {
                    DAY -> HOUR_IN_SECONDS
                    WEEK -> DAY_IN_SECONDS
                    YEAR -> MONTH_IN_SECONDS
                }
            }
            marker = BitcoinMarkerView(context, R.layout.chart_marker_view)
            animateX(ANIM_DURATION, Easing.EaseInCubic)
            description.isEnabled = false
            setDrawMarkers(true)
            invalidate()
        }
    }

    private fun formatForDay(value: Float) = getFormatter(value, DAY_FORMAT)

    private fun formatForWeek(value: Float) = getFormatter(value, WEEK_FORMAT)

    private fun formatForYear(value: Float) = getFormatter(value, YEAR_FORMAT)

    private fun getFormatter(value: Float, pattern: String = WEEK_FORMAT) = DateTimeFormat.forPattern(pattern).run {
        print(DateTime(value.toLong() * SEC_TO_MILLIS, DateTimeZone.UTC))
    }

    /**
     * Show progress bar
     */
    private fun pushProgress() {
        progressBar.show()
        mainContent.hide()
        btRetry.hide()
        tvNoConnection.hide()
    }

    /**
     * Show "No Connection" UI and hide the chart, time span buttons & progress bar.
     * I think there is a bug in ConstraintLayout groups + visibility because sometimes it
     * does not work. That is why I change visibility individually here, not using CL groups
     */
    private fun displayNoConnectionMessage() {
        btRetry.show()
        tvNoConnection.show()
        lineChart.hide()
        btDay.hide()
        btWeek.hide()
        btYear.hide()
        progressBar.hide()
    }

    /**
     * Hide "No Connection" UI and show the chart and timespan buttons.
     * I think there is a bug in ConstraintLayout groups + visibility because sometimes it
     * does not work. That is why I change visibility individually here, not using CL groups
     */
    private fun displayMainContent() {
        mainContent.show()
        lineChart.show()
        btDay.show()
        btWeek.show()
        btYear.show()
        btRetry.hide()
        tvNoConnection.hide()
        progressBar.hide()
    }

    private fun Button.unselect() {
        this.setTextColor(getColorFromAttr(R.attr.unselectedButtonText))
    }

    private fun Button.select() {
        this.setTextColor(getColorFromAttr(R.attr.selectedButtonText))
    }

    companion object {
        const val DAY_IN_SECONDS = 86400f
        const val HOUR_IN_SECONDS = 3600f
        const val MONTH_IN_SECONDS = 2628000f
        const val ANIM_DURATION = 1000
        const val SEC_TO_MILLIS = 1000
        const val YEAR_FORMAT = "MMM YYYY"
        const val WEEK_FORMAT = "MMM dd"
        const val DAY_FORMAT = "HH:mm"
        const val CHART_LABEL = "Bitcoin exchange rates"
    }
}
