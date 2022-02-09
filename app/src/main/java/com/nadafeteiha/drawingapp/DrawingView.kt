package com.nadafeteiha.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.ViewConfiguration
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class DrawingView (context: Context , attrs: AttributeSet): View(context , attrs){

    private lateinit var mCanvasBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    // contain the resource id for the shape should draw
    private var actionDrawing = -1
    // default color
    private var drawColor = Color.BLACK
    // last x and y that user touched and the color
    private var currentDrawPath : CustomPath = CustomPath(drawColor)
    // save all paths that been draw
    private val mDrawPaths = ArrayList<CustomPath>()
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var currentX = 0f
    private var currentY = 0f

    private val mDrawPaint = Paint().apply {
        color = drawColor
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = Constants.STROKE_WIDTH
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::mCanvasBitmap.isInitialized)
            mCanvasBitmap.recycle()

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap, 0f, 0f, null)
        for (p in mDrawPaths) {
            mDrawPaint.color = p.color
            canvas.drawPath(p, mDrawPaint)
        }
        if (!currentDrawPath.isEmpty) {
            mDrawPaint.color = currentDrawPath.color
            canvas.drawPath(currentDrawPath, mDrawPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Touch event of X coordinate
        val touchX = event.x

        // touch event of Y coordinate
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
               startDrawing(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                if (actionDrawing == Constants.ACTION_PENCIL){
                   pointerMoving(touchX, touchY)
                }
            }

            MotionEvent.ACTION_UP -> {
               drawShape(touchX, touchY)
                // Add the current path to the drawing so far
                mDrawPaths.add(currentDrawPath)
                // Reset the path so it doesn't get drawn again.
                currentDrawPath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    /*
    * this function take x and y points and draw the shape that been selected
    * actionDrawing contain the resource id for the shape that should draw
    * */
    private fun drawShape(touchX: Float, touchY: Float) {
        when(actionDrawing){
            Constants.ACTION_RECTANGLE ->{
                mCanvas.drawRect(currentX, currentY,
                    touchX, touchY, mDrawPaint)
            }

            Constants.ACTION_ELLIPSE ->{
                mCanvas.drawOval(currentX, currentY,
                    touchX, touchY, mDrawPaint)
            }

            Constants.ACTION_ARROW -> {
                val dx: Float = touchX - currentX
                val dy: Float = touchY - currentY
                val rad = atan2(dy.toDouble(), dx.toDouble()).toFloat()

                mCanvas.drawLine(currentX, currentY, touchX, touchY, mDrawPaint)
                mCanvas.drawLine(
                    touchX, touchY,
                    (touchX + cos(rad + Math.PI * 0.75) * 20).toFloat(),
                    (touchY + sin(rad + Math.PI * 0.75) * 20).toFloat(),
                    mDrawPaint
                )
                mCanvas.drawLine(
                    touchX, touchY,
                    (touchX + cos(rad - Math.PI * 0.75) * 20).toFloat(),
                    (touchY + sin(rad - Math.PI * 0.75) * 20).toFloat(),
                    mDrawPaint
                )
            }
        }
    }

    private fun pointerMoving(touchX: Float, touchY: Float) {
        val dx = abs(touchX - currentX)
        val dy = abs(touchY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            currentDrawPath.quadTo(currentX, currentY, (touchX + currentX) / 2,
                (touchY + currentY) / 2)
            currentX = touchX
            currentY = touchY
            // Draw the path in the extra bitmap to cache it.
            mCanvas.drawPath(currentDrawPath, mDrawPaint)
        }
    }

    private fun startDrawing(touchX: Float, touchY: Float) {
        currentDrawPath.color = drawColor
        // Clear any lines and curves from the path, making it empty.
        currentDrawPath.reset()
        // Set the beginning of the next contour to the point (x,y).
        currentDrawPath.moveTo(touchX, touchY)

        currentX = touchX
        currentY = touchY
    }

    /*
    * This function called after the user change/select new
    * shape to draw with it
    * Input drawingTool: is the resource id that been selected
    * */
    internal fun setDrawingTool(drawingTool: Int){
       actionDrawing = drawingTool
    }

    /*
    * this function called after user change color
    * responsible to change Variable color for the path  to selected color
    */
    internal fun setColor(newColor: String) {
        drawColor = Color.parseColor(newColor)
        mDrawPaint.color = drawColor
    }

    // class for contain the path and each color fot this path
    internal inner class CustomPath(var color: Int) : Path()
}