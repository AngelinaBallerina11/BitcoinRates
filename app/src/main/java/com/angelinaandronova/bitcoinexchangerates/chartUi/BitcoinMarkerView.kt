package com.angelinaandronova.bitcoinexchangerates.chartUi

import android.content.Context
import com.angelinaandronova.bitcoinexchangerates.R
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.chart_marker_view.view.*
import java.util.*
import kotlin.math.round


class BitcoinMarkerView(val ctx: Context, layoutResource: Int) : MarkerView(ctx, layoutResource), IMarker {

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        tvContent.text =
            String.format(Locale.getDefault(), ctx.resources.getString(R.string.bitcoin_value), entry.y.round(3))
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())

}


fun Float.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}