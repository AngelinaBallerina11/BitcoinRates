package com.angelinaandronova.bitcoinexchangerates

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.angelinaandronova.bitcoinexchangerates.MainViewModel.ScreenState.*
import com.angelinaandronova.bitcoinexchangerates.MainViewModel.TimeSpan.*
import com.angelinaandronova.bitcoinexchangerates.chartUi.BitcoinMarkerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushProgress()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.screenState.observe(this, Observer {
            when (it) {
                is NoConnection -> {
                    displayNoConnectionMessage()
                    Log.i("ANGELINA1234", "no connection")
                }
                is Loading -> {
                    pushProgress()
                    viewModel.loadData(it.timespan)
                    Log.i("ANGELINA1234", "loading")
                }
                is DisplayData -> {
                    popProgress()
                    displayMainContent()
                    Log.i("ANGELINA1234", "display data")
                    setUpChart(it.chartEntries)
                    setUpButtons(it.chartEntries.first)
                }
            }
        })

        btDay.setOnClickListener { viewModel.screenState.value = Loading(DAY) }
        btWeek.setOnClickListener { viewModel.screenState.value = Loading(WEEK) }
        btYear.setOnClickListener { viewModel.screenState.value = Loading(YEAR) }
        btRetry.setOnClickListener { viewModel.tryToLoadData() }
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
        val dataSet = LineDataSet(chartEntries.second, "Bitcoin exchange rates").apply {
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
            markerView = BitcoinMarkerView(context, R.layout.chart_marker_view)
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

    private fun pushProgress() {
        mainContent.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        btRetry.visibility = View.INVISIBLE
        tvNoConnection.visibility = View.INVISIBLE
    }

    private fun popProgress() {
        mainContent.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        btRetry.visibility = View.INVISIBLE
        tvNoConnection.visibility = View.INVISIBLE
    }

    /**
     * Show "No Connection" UI and hide the chart, timespan buttons & progress bar.
     * I think there is a bug in ConstraintLayout groups + visibility because sometimes it
     * does not work. That is why I change visibility individually here, not using CL groups
     */
    private fun displayNoConnectionMessage() {
        lineChart.visibility = View.INVISIBLE
        btDay.visibility = View.INVISIBLE
        btWeek.visibility = View.INVISIBLE
        btYear.visibility = View.INVISIBLE
        btRetry.visibility = View.VISIBLE
        tvNoConnection.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    /**
     * Hide "No Connection" UI and show the chart and timespan buttons.
     * I think there is a bug in ConstraintLayout groups + visibility because sometimes it
     * does not work. That is why I change visibility individually here, not using CL groups
     */
    private fun displayMainContent() {
        lineChart.visibility = View.VISIBLE
        btDay.visibility = View.VISIBLE
        btWeek.visibility = View.VISIBLE
        btYear.visibility = View.VISIBLE
        btRetry.visibility = View.INVISIBLE
        tvNoConnection.visibility = View.INVISIBLE
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
    }
}
