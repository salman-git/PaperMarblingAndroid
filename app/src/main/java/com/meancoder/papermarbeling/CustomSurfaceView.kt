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
import java.util.Stack
import kotlin.random.Random

class CustomSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private val TAG: String = CustomSurfaceView::javaClass.javaClass.simpleName

    private lateinit var firstPoint: PointF

    // Variable to store the current drawing mode
    private var currentDrawingMode = DrawingMode.MODE_NORMAL
    private var currentTineTool: PEN_TYPE? = PEN_TYPE.PEN
    private var drawingThread: DrawingThread? = null
    private var drops: MutableList<Drop>? = null
    private var undoStack: Stack<Drop> = Stack()
    private var redoStack: Stack<Drop> = Stack()
    private var lastX: Float? = null
    private var lastY: Float? = null
    private var bk: Int = Color.rgb(252, 255, 255)
    private var drawingColor: Int = Color.rgb(0, 0, 0)
    private var isRandomDrawingColor: Boolean = true

    private var radius:Float = 100f
    private var spiralRadius:Float = 100f
    private var combWidth:Float = 100f

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
                if (currentDrawingMode == DrawingMode.MODE_NORMAL) {
                    val paint = Paint().apply {
                        color = if (isRandomDrawingColor) getRandomColor() else drawingColor
                        isAntiAlias = true
                        style = Paint.Style.FILL
                    }
                    dropThread = DropThread(x, y, paint) { x, y, r, paint ->
                        addInk(x, y, r, paint)
                    }
                    dropThread.start()
                } else if (currentDrawingMode == DrawingMode.MODE_TINE) {
                    if(currentTineTool == PEN_TYPE.CIRCULAR_CLOCKWISE ||
                        currentTineTool == PEN_TYPE.CIRCULAR_ANTICLOCKWISE ||
                        currentTineTool  == PEN_TYPE.SPIRAL_ANTICLOCKWISE ||
                        currentTineTool == PEN_TYPE.SPIRAL_CLOCKWISE
                        ) {
                        dropThread = DropThread(x, y, Paint()) { x, y, r, paint ->
                            circularTine(x, y, 4f, 32f, radius, spiralRadius,
                                isClockwise = currentTineTool == PEN_TYPE.CIRCULAR_CLOCKWISE || currentTineTool == PEN_TYPE.SPIRAL_CLOCKWISE,
                                isSpiral=currentTineTool == PEN_TYPE.SPIRAL_CLOCKWISE || currentTineTool == PEN_TYPE.SPIRAL_ANTICLOCKWISE)
                        }
                        dropThread.start()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if(dropThread.isAlive)
                    dropThread.stopThread()
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentDrawingMode == DrawingMode.MODE_NORMAL)
                    dropThread.updateXY(x, y)

                if (currentDrawingMode == DrawingMode.MODE_TINE) {
                    val X = if (Math.abs(firstPoint.x - x) < 50) firstPoint.x else x
                    val Y = if (Math.abs(firstPoint.y - y) < 50) firstPoint.y else y

                    val dx = X - lastX!!
                    val dy = Y - lastY!!
                    val mag = Math.sqrt(dx.toDouble() * dx + dy * dy)
                    if (mag > 0.1) {
                        val vector = PointF((dx / mag).toFloat(), (dy / mag).toFloat())
                        if (currentTineTool == PEN_TYPE.COMB_VERTICAL) {
//                            combTine(vector, X, Y, 80f, 8f, 100f)
                            val sliceSize = combWidth.toInt()
                            var xLeft = firstPoint.x
                            var xRight = firstPoint.x

                            while (xLeft > 0 || xRight < width) {
                                if (xLeft > 0) {
                                    tineLine(vector, xLeft.toFloat(), 0f, 2f, 16f)
                                    xLeft -= sliceSize
                                }
                                if (xRight < width) {
                                    tineLine(vector, xRight.toFloat(), 0f, 2f, 16f)
                                    xRight += sliceSize
                                }
                            }
                        } else if (currentTineTool == PEN_TYPE.COMB_HORIZONTAL) {
                            val sliceSize = combWidth.toInt()
                            var yUp = firstPoint.y
                            var yDown = firstPoint.y

                            while (yUp > 0 || yDown < height) {
                                if (yUp > 0) {
                                    tineLine(vector, 0f, yUp.toFloat(), 2f, 16f)
                                    yUp -= sliceSize
                                }
                                if (yDown < height) {
                                    tineLine(vector, 0f, yDown.toFloat(), 2f, 16f)
                                    yDown += sliceSize
                                }
                            }

                        } else if (currentTineTool == PEN_TYPE.PEN) {
                            tineLine(vector, X, Y, 2f, 16f)
                        }
                    }
                    lastX = X
                    lastY = Y
                }
            }
        }
        return true
    }

    private fun addInk(x: Float, y: Float, r: Float, paint: Paint) {
        val drop = Drop(x, y, r, paint)
        for (other in drops!!) {
            other.marble(drop)
        }
        drops!!.add(drop)
        undoStack.push(drop)
        redoStack.clear()
    }

    private fun tineLine(m: PointF, x: Float, y: Float, z: Float, c: Float) {
        for (other in drops!!) {
            other.tine(m, x, y, z, c)
        }
    }

    private fun circularTine(
        x: Float,
        y: Float,
        z: Float,
        c: Float,
        r: Float = 100f,
        spiralRadius:Float=100f,
        isClockwise: Boolean,
        isSpiral:Boolean
    ) {
        val radius = if (isSpiral) spiralRadius else r
        for (other in drops!!) {

            other.circularTine(x, y, z, c, radius, isClockwise, isSpiral)
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

    private fun getRandomColor(): Int {
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
        undoStack.clear()
        redoStack.clear()
    }

    fun setComb(comb: PEN_TYPE) {
        currentTineTool = comb
    }

    fun setBKColor(color: Int) {
        //the background color of canvas
        bk = color
    }

    fun setRandomColor(value: Boolean) {
        isRandomDrawingColor = value
    }

    fun isRandomColorActive(): Boolean {
        return isRandomDrawingColor
    }

    fun setDrawingColor(color: Int) {
        isRandomDrawingColor = false
        drawingColor = color
    }

    fun setCombWidth(value:Float) {
        combWidth = value
    }
    fun setSpeed(value:Float){
        spiralRadius=value
    }
    fun setRadius(value:Float) {
        radius = value
    }

    fun isDrawModeActive(): Boolean {
        return currentDrawingMode == DrawingMode.MODE_NORMAL
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val lastDrop = undoStack.pop()
            drops?.remove(lastDrop)
            redoStack.push(lastDrop)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val lastUndoneDrop = redoStack.pop()
            drops?.add(lastUndoneDrop)
            undoStack.push(lastUndoneDrop)
        }
    }

    fun getCurrentTineTool(): PEN_TYPE? {
        return currentTineTool
    }
}
