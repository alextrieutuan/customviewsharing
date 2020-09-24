package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_clock.view.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class CompoundClockView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val dateFormatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)

    private var tickFlag = false
    private var is24h = false

    private val updateThread = object : ViewUpdateThread(
        WeakReference(this),
        500
    ) {
        override fun doUpdate() {
            updateTime()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_clock, this)
        text_hour.setOnClickListener {
            toggleAMPM()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateThread.startThread()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updateThread.stopThread()
    }

    private fun updateTime() {
        val calendar: Calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val isAm = hour < 12
        hour = if (is24h) hour else {
            if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        }

        val min = calendar.get(Calendar.MINUTE)
        val sec = calendar.get(Calendar.SECOND)

        text_hour.text = hour.toString()
        text_min.text = min.withLeadingZero()
        text_sec.text = sec.withLeadingZero()
        if (!is24h) {
            text_ampm.text = if (isAm) "am" else "pm"
        }
        text_date.text = dateFormatter.format(calendar.time)

        text_tick.visibility = tickFlag.toVisibility()

        tickFlag = !tickFlag
    }

    private fun Int.withLeadingZero(): String {
        return if (this < 10) "0$this" else "$this"
    }

    private fun Boolean.toVisibility(): Int {
        return if (this) View.VISIBLE else View.INVISIBLE
    }

    private fun toggleAMPM() {
        is24h = !is24h
        text_ampm.visibility = (!is24h).toVisibility()
        updateTime()
    }
}


