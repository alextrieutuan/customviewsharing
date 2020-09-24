package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var currentHeight: Int = 0
    private var currentWidth: Int = 0
    private var minDimension: Int = 0
    private var frameRadius: Float = 0f
    private var numberCircleRadius: Float = 0f
    private var centerPointRadius: Float = 0f
    private var hourHandSize: Float = 0f
    private var minuteHandSize: Float = 0f
    private var minuteSecondSize: Float = 0f

    private lateinit var centerPoint: PointF
    private var initialised = false

    private val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    private val fillPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }

    private val strokePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
        }
    }

    private val textPaint : Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 40.0f
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeDimensions(w, h)
    }

    private fun initializeDimensions(w: Int, h: Int) {
        currentWidth = w
        currentHeight = h
        minDimension = min(currentWidth, currentHeight)
        centerPoint = PointF(currentWidth / 2.0f, currentHeight / 2.0f)
        frameRadius = (minDimension - paddingStart - paddingBottom) / 2.0f
        numberCircleRadius = frameRadius - frameRadius / 10
        centerPointRadius = frameRadius / 15.0f
        hourHandSize = frameRadius - frameRadius / 2.0f
        minuteHandSize = frameRadius - frameRadius / 3.0f
        minuteSecondSize = frameRadius - frameRadius / 4.0f
        initialised = true
    }

    override fun onDraw(canvas: Canvas) {
        if (!initialised) {
            return
        }
        drawFrame(canvas)
        drawHand(canvas)
        invalidate()
        postInvalidateDelayed(500)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("ClockView", "The view is too small, the content might get cut")
        }
        return result
    }

    private fun drawFrame(canvas: Canvas) {
        fillPaint.color = COLOR_PURPLE
        canvas.drawCircle(centerPoint.x, centerPoint.y, frameRadius, fillPaint)
        //drawNumerals(canvas)
    }

    private fun drawHand(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour

        val min = calendar.get(Calendar.MINUTE)
        val sec = calendar.get(Calendar.SECOND)

        val normalizeHour = (hour + min / 60.0) * 5f
        drawHourHand(canvas, normalizeHour)
        drawMinuteHand(canvas, min.toDouble())
        drawSecondHand(canvas, sec.toDouble())

        fillPaint.color = Color.WHITE
        canvas.drawCircle(centerPoint.x, centerPoint.y, centerPointRadius, fillPaint)
    }

    private fun drawHourHand(canvas: Canvas, value: Double) {
        strokePaint.color = Color.WHITE
        strokePaint.strokeWidth = centerPointRadius / 1.5f

        val angle = Math.PI * value / 30 - Math.PI / 2
        canvas.drawLine(
            centerPoint.x,
            centerPoint.y,
           (centerPoint.x + cos(angle) * hourHandSize).toFloat(),
            (centerPoint.y + sin(angle) * hourHandSize).toFloat(),
            strokePaint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, value: Double) {
        strokePaint.color = Color.WHITE
        strokePaint.strokeWidth = centerPointRadius / 2.0f

        val angle = Math.PI * value / 30 - Math.PI / 2
        canvas.drawLine(
            centerPoint.x,
            centerPoint.y,
            (centerPoint.x + cos(angle) * minuteHandSize).toFloat(),
            (centerPoint.y + sin(angle) * minuteHandSize).toFloat(),
            strokePaint
        )
    }

    private fun drawSecondHand(canvas: Canvas, value: Double) {
        strokePaint.color = Color.RED
        strokePaint.strokeWidth = centerPointRadius / 2.5f

        val angle = Math.PI * value / 30 - Math.PI / 2
        canvas.drawLine(
            centerPoint.x,
            centerPoint.y,
            (centerPoint.x + cos(angle) * minuteSecondSize).toFloat(),
            (centerPoint.y + sin(angle) * minuteSecondSize).toFloat(),
            strokePaint
        )
    }

    private fun drawNumerals(canvas: Canvas) {
        val rect = Rect()
        for (number in numbers) {
            val num = number.toString()
            textPaint.getTextBounds(num, 0, num.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = centerPoint.x + cos(angle) * numberCircleRadius - rect.width() / 2
            val y = centerPoint.y + sin(angle) * numberCircleRadius + rect.height() / 2
            canvas.drawText(num, x.toFloat(), y.toFloat(), textPaint)
        }
    }

    companion object {
        private val COLOR_PURPLE = Color.parseColor("#4c11ba")
    }
}