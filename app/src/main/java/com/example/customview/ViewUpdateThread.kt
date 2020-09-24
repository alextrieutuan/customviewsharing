package com.example.customview

import android.view.View
import java.lang.ref.WeakReference

abstract class ViewUpdateThread(
    private val view: WeakReference<View>,
    private val intervalInMillis: Long
) : Thread() {

    private var isRunning = false

    fun startThread() {
        isRunning = true
        start()
    }

    fun stopThread() {
        isRunning = false
        try {
            this.interrupt()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        while (isRunning) {
            view.get()?.let {
                it.post {
                    doUpdate()
                }
            }

            try {
                sleep(intervalInMillis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    abstract fun doUpdate()
}