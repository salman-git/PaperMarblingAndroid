package com.meancoder.papermarbeling

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

class CustomSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private val TAG: String = CustomSurfaceView::javaClass.javaClass.simpleName


    private lateinit var firstPoint: PointF

    // Variable to store the current drawing mode
    private var currentDrawingMode = DrawingMode.MODE_NORMAL
    private var currentCombType: PEN_TYPE? = PEN_TYPE.PEN
    private var drawingThread: DrawingThread? = null
    private var drops: MutableList<Drop>? = null
    private var lastX:Float? = null
    private var lastY:Float? = null
    private var bk: Int = Color.rgb(252, 255, 255)
    private var drawingColor: Int = Color.rgb(0, 0, 0)
    private var isRandomDrawingColor:Boolean = true


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
                firstPoint = PointF(x, y)
                lastX = x
                lastY = y
                if(currentDrawingMode == DrawingMode.MODE_NORMAL) {
                    val paint = Paint().apply {
                        color = if (isRandomDrawingColor) getRandomColor() else drawingColor
                        isAntiAlias = true
                        style = Paint.Style.FILL
                    }
                    dropThread = DropThread(x, y, paint) { x, y, r, paint ->
                        addInk(x, y, r, paint)
                    }
                    dropThread.start()
                }
            }
            MotionEvent.ACTION_UP -> {
                dropThread.stopThread()
            }
            MotionEvent.ACTION_MOVE -> {
                if(currentDrawingMode == DrawingMode.MODE_NORMAL)
                    dropThread.updateXY(x, y)

                if (currentDrawingMode == DrawingMode.MODE_TINE) {
                    val X = if(Math.abs(firstPoint.x - x) < 50) firstPoint.x else x
                    val Y = if(Math.abs(firstPoint.y - y) < 50) firstPoint.y else y

                    val dx = X - lastX!!
                    val dy = Y - lastY!!
                    val mag = Math.sqrt(dx.toDouble() * dx + dy * dy)
                    if (mag > 0.1) {
                        val vector = PointF((dx / mag).toFloat(), (dy / mag).toFloat())
                        if (currentCombType == PEN_TYPE.COMB_VERTICAL) {
                            for (Xc in 0..width step 100)
                                tineLine(vector, Xc.toFloat(), 0f, 2f, 16f)
                        }else if (currentCombType == PEN_TYPE.COMB_HORIZONTAL) {
                            for (Yc in 0..height step 100)
                                tineLine(vector, 0f, Yc.toFloat(), 2f, 16f)
                        } else if (currentCombType == PEN_TYPE.PEN){
                            tineLine(vector, X, Y, 4f, 32f)
                        }
                    }
                    lastX = X
                    lastY = Y
                }
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

    fun tineLine(m: PointF, x: Float, y: Float, z: Float, c: Float) {
        for (other in drops!!) {
            other.tine(m, x, y, z, c)
        }
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
        return drawingPalette[Random.nextInt(drawingPalette.size)]
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

    fun toggleDrawingMode() {
        currentDrawingMode = if (currentDrawingMode == DrawingMode.MODE_NORMAL) {
            DrawingMode.MODE_TINE
        } else {
            DrawingMode.MODE_NORMAL
        }
    }

    fun getDropsSize(): Int {
        return drops!!.size
    }

    fun clear() {
        drops?.clear()
    }

    fun setComb(comb: PEN_TYPE) {
        currentCombType = comb
    }

    fun setBKColor(color: Int) {
        //the background color of canvas
        bk=color
    }

    fun setRandomColor(value:Boolean) {
        isRandomDrawingColor = value
    }

    fun isRandomColorActive(): Boolean {
        return isRandomDrawingColor
    }

    fun setDrawingColor(color: Int) {
        isRandomDrawingColor = false
        drawingColor=color
    }

    fun isDrawModeActive(): Boolean {
        return currentDrawingMode == DrawingMode.MODE_NORMAL
    }

}
