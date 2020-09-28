package com.example.customview

import android.content.Context
import android.util.AttributeSet
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

    private var isReverse = false
        set(value) {
            if (field != value) {
                field = value
                reverseCalendar = null
                updateReverseUI()
            }
        }

    private var reverseCalendar: Calendar? = null

    private val updateThread = object : ViewUpdateThread(WeakReference(this), 1000) {
        override fun doUpdate() {
            if (!isReverse) {
                updateTime()
            } else {
                if (reverseCalendar == null) {
                    reverseCalendar = Calendar.getInstance()
                }
                reverseCalendar!!.timeInMillis = reverseCalendar!!.timeInMillis - 1000
                updateTime(reverseCalendar!!)
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_clock, this)
        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CompoundClockView,
            0,
            0
        )

        try {
            is24h = attributes.getBoolean(
                R.styleable.CompoundClockView_compound_clock_view_is_24h,
                false
            )
            isReverse = attributes.getBoolean(
                R.styleable.CompoundClockView_compound_clock_view_is_reverse,
                false
            )
        } finally {
            attributes.recycle()
        }

        text_hour.setOnClickListener {
            toggleAMPM()
        }

        image_reverse.setOnClickListener {
            toggleReverse()
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

    fun toggleReverse() {
        isReverse = !isReverse
    }

    private fun updateReverseUI() {
        val imgRes =
            if (isReverse) android.R.drawable.ic_media_rew else android.R.drawable.ic_media_ff
        image_reverse.setImageResource(imgRes)
        invalidate()
    }

    private fun updateTime(calendar: Calendar = Calendar.getInstance()) {
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val isAm = hour < 12
        hour = if (is24h) hour else {
            if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        }

        val min = calendar.get(Calendar.MINUTE)
        val sec = calendar.get(Calendar.SECOND)

        text_ampm.visibility = (!is24h).toVisibility()
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
        updateThread.doUpdate()
    }
}


