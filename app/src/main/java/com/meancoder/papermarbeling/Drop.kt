package com.meancoder.papermarbeling

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import androidx.core.graphics.minus
import java.lang.Math.abs
import kotlin.math.sqrt


class Drop(x: Float, y: Float, r: Float, paint: Paint) {
    private val center: PointF
    private val radius: Float
    private val circleDetail: Int
    private val vertices: MutableList<PointF>
    private lateinit var dropPaint: Paint

    init {
        center = PointF(x, y)
        radius = r
        circleDetail = 200 // Or calculate based on radius
        vertices = ArrayList()
        dropPaint = paint
        for (i in 0 until circleDetail) {
            val angle: Float = Utils.map(i.toFloat(), 0f, circleDetail.toFloat(), 0f, (Math.PI * 2).toFloat())
            val vx = (Math.cos(angle.toDouble()) * radius).toFloat() + center.x
            val vy = (Math.sin(angle.toDouble()) * radius).toFloat() + center.y
            vertices.add(PointF(vx, vy))
        }
    }

    fun tine(m: PointF, x: Float, y: Float, z: Float, c: Float) { //old
        val u = 1 / Math.pow(2.0, (1 / c).toDouble()).toFloat()
        val b = PointF(x, y)
        for (v in vertices) {
            val n = PointF(m.y, -m.x) // Rotate 90 degrees
            val pb = v.minus(b)
            val d = Math.abs(pb.x * n.x + pb.y * n.y)
            val mag = z * Math.pow(u.toDouble(), d.toDouble()).toFloat()
            v.x += m.x * mag
            v.y += m.y * mag
        }
    }

    fun circularTine(x:Float, y:Float, z:Float, c:Float, r:Float, clockWise:Boolean=true) {
        val C = PointF(x, y) //center of arc
        val u = 1 / Math.pow(2.0, (1 / c).toDouble()).toFloat()
        for (v in vertices) {
            val mag = sqrt((v.x - C.x) * (v.x - C.x) + (v.y - C.y) * (v.y - C.y)) //h

            val d = Math.abs(mag - r)
            val l = z * Math.pow(u.toDouble(), (r * -1f).toDouble())//z * Math.pow(u.toDouble(), d.toDouble())
            var alpha = l / mag
            val translatedX = v.x - C.x
            val translatedY = v.y - C.y

            if(!clockWise) {
                alpha *= -1f
            }
            // Apply rotation
            val cosAlpha = Math.cos(alpha)
            val sinAlpha = Math.sin(alpha)
            val rotatedX = translatedX * cosAlpha - translatedY * sinAlpha
            val rotatedY = translatedX * sinAlpha + translatedY * cosAlpha

            // Translate back to original position
            if (mag > 0) {
                v.x = (rotatedX + C.x).toFloat()
                v.y = (rotatedY + C.y).toFloat()
            } else {
                v.x = C.x
                v.y = C.y
            }
        }
    }

    fun marble(other: Drop) {
        for (v in vertices) {
            val c = other.center
            val r = other.radius
            val p = PointF(v.x - c.x, v.y - c.y)
            val m = p.length()
            val root = Math.sqrt((1 + r * r / (m * m)).toDouble()).toFloat()
            p.x *= root
            p.y *= root
            p.x += c.x
            p.y += c.y
            v[p.x] = p.y
        }
    }

    fun show(canvas: Canvas) {
        val path = Path()
        path.moveTo(vertices[0].x, vertices[0].y)
        for (i in 1 until vertices.size) {
            path.lineTo(vertices[i].x, vertices[i].y)
        }
        path.close()
        canvas.drawPath(path, dropPaint)
    }
}
