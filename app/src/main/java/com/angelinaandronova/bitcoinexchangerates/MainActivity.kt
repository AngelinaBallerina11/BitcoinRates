package com.angelinaandronova.bitcoinexchangerates

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        viewModel.chartEntries.observe(this, Observer { data ->
            data?.let {
                Log.i("ANGELINA1234", "data $it")
                setUpChart(it.second)
                popProgress()
            }
        })

        viewModel.timespan.observe(this, Observer {
            Log.i("ANGELINA1234", "timespan $it")
            if (it == viewModel.chartEntries.value?.first) return@Observer
            pushProgress()
            viewModel.loadData(it)
            when (it) {
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
        })

        btDay.setOnClickListener { viewModel.timespan.value = DAY }
        btWeek.setOnClickListener { viewModel.timespan.value = WEEK }
        btYear.setOnClickListener { viewModel.timespan.value = YEAR }
    }

    private fun Button.unselect() {
        this.setTextColor(getColorFromAttr(R.attr.unselectedButtonText))
    }

    private fun Button.select() {
        this.setTextColor(getColorFromAttr(R.attr.selectedButtonText))
    }

    private fun setUpChart(chartEntries: ArrayList<Entry>) {
        val dataSet = LineDataSet(chartEntries, "Bitcoin exchange rates").apply {
            color = getColorFromAttr(R.attr.chartLineColor)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        val xAxisFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String =
                when (viewModel.timespan.value) {
                    DAY -> formatForDay(value)
                    WEEK -> formatForWeek(value)
                    YEAR -> formatForYear(value)
                    else -> formatForWeek(value)
                }

        }

        with(lineChart) {
            data = LineData(dataSet)
            xAxis.run {
                valueFormatter = xAxisFormatter
                granularity = when (viewModel.timespan.value) {
                    DAY -> HOUR_IN_SECONDS
                    WEEK -> DAY_IN_SECONDS
                    YEAR -> MONTH_IN_SECONDS
                    else -> DAY_IN_SECONDS
                }
            }
            markerView = BitcoinMarkerView(context, R.layout.chart_marker_view)
            animateX(ANIM_DURATION, Easing.EaseInCubic)
            description.isEnabled = false
            setDrawMarkers(true)
            invalidate()
        }
    }

    private fun formatForDay(value: Float): String {
        val fmt = DateTimeFormat.forPattern("HH:mm")
        return fmt.print(DateTime(value.toLong() * SEC_TO_MILLIS, DateTimeZone.UTC))
    }

    private fun formatForWeek(value: Float): String {
        val fmt = DateTimeFormat.forPattern("MMM dd")
        return fmt.print(DateTime(value.toLong() * SEC_TO_MILLIS, DateTimeZone.UTC))
    }

    private fun formatForYear(value: Float): String {
        val fmt = DateTimeFormat.forPattern("MMM YYYY")
        return fmt.print(DateTime(value.toLong() * SEC_TO_MILLIS, DateTimeZone.UTC))
    }

    private fun pushProgress() {
        lineChart.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun popProgress() {
        lineChart.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    companion object {
        const val DAY_IN_SECONDS = 86400f
        const val HOUR_IN_SECONDS = 3600f
        const val MONTH_IN_SECONDS = 2628000f
        const val ANIM_DURATION = 1000
        const val SEC_TO_MILLIS = 1000
    }
}
