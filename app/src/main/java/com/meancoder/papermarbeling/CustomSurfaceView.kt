package com.meancoder.papermarbeling

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import kotlin.random.Random

class CustomSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private val TAG: String = CustomSurfaceView::javaClass.javaClass.simpleName
    private var drawingThread: DrawingThread? = null
    private var drops: MutableList<Drop>? = null
    private var palette: MutableList<Int> = mutableListOf(
        Color.rgb(11, 106, 136),
        Color.rgb(45, 197, 244),
        Color.rgb(112, 50, 126),
        Color.rgb(146, 83, 161),
        Color.rgb(164, 41, 99),
        Color.rgb(236, 1, 90),
        Color.rgb(240, 99, 164),
        Color.rgb(241, 97, 100),
        Color.rgb(248, 158, 79)
    )
    private var bk: Int = Color.rgb(252, 238, 33)

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        holder.addCallback(this)
        drops = mutableListOf()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingThread = DrawingThread(getHolder(), this)
        drawingThread!!.setRunning(true)
        drawingThread!!.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Not implemented
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        drawingThread!!.setRunning(false)
        while (retry) {
            try {
                drawingThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    private lateinit var dropThread: DropThread
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val paint = Paint().apply {
                    color = getRandomColor()
                    isAntiAlias = true
                    style = Paint.Style.FILL
                }
                dropThread = DropThread(x, y, paint) { x, y, r, paint ->
                    addInk(x, y, r, paint)
                }
                dropThread.start()
            }
            MotionEvent.ACTION_UP -> {
                dropThread.stopThread()
            }
            MotionEvent.ACTION_MOVE -> {
                dropThread.updateXY(x, y)
            }
        }

        return true
    }

    fun addInk(x: Float, y: Float, r: Float, paint: Paint) {
        val drop = Drop(x, y, r, paint)
        for (other in drops!!) {
            other.marble(drop)
        }
        drops!!.add(drop)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(bk)
        val dropsCopy = drops?.toList() ?: return
        for (drop in dropsCopy) {
            drop.show(canvas)
        }
    }

    fun getRandomColor(): Int {
        return palette[Random.nextInt(palette.size)]
    }

    class DropThread(private var x: Float, private var y: Float, private val paint: Paint, private val addInkCallback: (Float, Float, Float, Paint) -> Unit) : Thread() {

        private var isRunning = true
        private var radiusMultiplier = 1f

        override fun run() {
            while (isRunning) {
                addInkCallback(x, y, 50f * radiusMultiplier, paint)
                radiusMultiplier += 0.05f
                sleep(50) // Adjust sleep duration as needed
            }
        }
        fun updateXY(x: Float, y: Float) {
            this.x = x
            this.y = y
            radiusMultiplier = 1f
        }

        fun stopThread() {
            radiusMultiplier = 1f
            isRunning = false
        }
    }

}
