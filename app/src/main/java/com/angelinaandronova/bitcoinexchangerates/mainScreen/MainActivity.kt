package com.angelinaandronova.bitcoinexchangerates.mainScreen

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.angelinaandronova.bitcoinexchangerates.R
import com.angelinaandronova.bitcoinexchangerates.chartUi.BitcoinMarkerView
import com.angelinaandronova.bitcoinexchangerates.di.ViewModelFactory
import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainViewModel.ScreenState.*
import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainViewModel.TimeSpan.*
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

        btWeek.setOnClickListener { mainViewModel.screenState.value = Loading(WEEK) }
        btMonth.setOnClickListener { mainViewModel.screenState.value = Loading(MONTH) }
        btYear.setOnClickListener { mainViewModel.screenState.value = Loading(YEAR) }
        btRetry.setOnClickListener { mainViewModel.tryToLoadData() }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setUpButtons(timeSpan: MainViewModel.TimeSpan) {
        when (timeSpan) {
            WEEK -> {
                btWeek.select()
                btMonth.unselect()
                btYear.unselect()
            }
            MONTH -> {
                btMonth.select()
                btWeek.unselect()
                btYear.unselect()
            }
            YEAR -> {
                btYear.select()
                btWeek.unselect()
                btMonth.unselect()
            }
        }
    }

    private fun setUpChart(chartEntries: Pair<MainViewModel.TimeSpan, ArrayList<Entry>>) {
        val dataSet = LineDataSet(chartEntries.second, resources.getString(R.string.chart_label)).apply {
            color = getColorFromAttr(R.attr.chartLineColor)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        val xAxisFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String =
                when (chartEntries.first) {
                    WEEK -> formatForWeek(value)
                    MONTH -> formatForMonth(value)
                    YEAR -> formatForYear(value)
                }

        }

        with(lineChart) {
            data = LineData(dataSet)
            xAxis.run {
                valueFormatter = xAxisFormatter
                granularity = when (chartEntries.first) {
                    WEEK, MONTH -> DAY_IN_SECONDS
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

    private fun formatForMonth(value: Float) = getFormattedLabel(value, MONTH_FORMAT)

    private fun formatForWeek(value: Float) = getFormattedLabel(value, WEEK_FORMAT)

    private fun formatForYear(value: Float) = getFormattedLabel(value, YEAR_FORMAT)

    private fun getFormattedLabel(value: Float, pattern: String = WEEK_FORMAT) =
        DateTimeFormat.forPattern(pattern).run {
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
        btWeek.hide()
        btMonth.hide()
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
        btMonth.show()
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
        const val MONTH_IN_SECONDS = 2628000f
        const val ANIM_DURATION = 1000
        const val SEC_TO_MILLIS = 1000
        const val YEAR_FORMAT = "MMM YYYY"
        const val WEEK_FORMAT = "MMM dd"
        const val MONTH_FORMAT = "MMM dd"
    }
}
