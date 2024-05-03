package com.meancoder.papermarbeling

import android.graphics.Canvas
import android.view.SurfaceHolder

class DrawingThread(
    private val surfaceHolder: SurfaceHolder,
    private val customSurfaceView: CustomSurfaceView
) :
    Thread() {
    private var isRunning = false
    fun setRunning(isRunning: Boolean) {
        this.isRunning = isRunning
    }

    override fun run() {
        var canvas: Canvas?
        while (isRunning) {
            canvas = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) { customSurfaceView.draw(canvas) }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}
