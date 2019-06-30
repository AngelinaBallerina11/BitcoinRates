package com.angelinaandronova.bitcoinexchangerates

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.lifecycle.MutableLiveData


fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

fun View.show() {
    if (this.visibility != View.VISIBLE) this.visibility = View.VISIBLE
}

fun View.hide() {
    if (this.visibility == View.VISIBLE) this.visibility = View.INVISIBLE
}

