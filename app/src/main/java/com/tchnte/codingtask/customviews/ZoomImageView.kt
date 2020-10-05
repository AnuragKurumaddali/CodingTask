package com.tchnte.codingtask.customviews

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import com.tchnte.codingtask.R

/**
 * Custom ImageView for circular images in Android while maintaining the
 * best draw performance and supporting custom borders & selectors.
 */
class ZoomImageView : ImageView, OnTouchListener {
    // these matrices will be used to move and zoom image
    private var zoomMatrix = Matrix()
    private val savedMatrix = Matrix()
    private var mode = NONE

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var d = 0f
    private var newRot = 0f
    private var lastEvent: FloatArray? = null
    var rotation = true
    var scaledown = true
    private var initStatus = false
    var init_scale = 0f
    private var gestureDetector: GestureDetector? = null
    private var zoomViewClick: ZoomViewClick? = null

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.styleable.ZoomImageView_ZoomImageViewDefault
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr)
    }

    fun setZoomViewClickListener(zoomImageViewClick: ZoomImageView.ZoomViewClick?) {
        this.zoomViewClick = zoomImageViewClick
    }

    fun setInitStatus(initStatus: Boolean) {
        this.initStatus = initStatus
    }

    /**
     * Initializes paint objects and sets desired attributes.
     * @param context Context
     * @param attrs Attributes
     * @param defStyle Default Style
     */
    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) {
        setOnTouchListener(this)
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.ZoomImageView, defStyle, 0)
        rotation = a.getBoolean(R.styleable.ZoomImageView_rotation, true)
        scaledown = a.getBoolean(R.styleable.ZoomImageView_scaledown, false)
        scaleType = ScaleType.MATRIX
        zoomMatrix = imageMatrix
        gestureDetector =
            GestureDetector(context, SingleTapConfirm())
    }

    override fun onDraw(canvas: Canvas) {
        if (!initStatus && drawable != null) {
            val drawableRect =
                RectF(
                    0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight
                        .toFloat()
                )
            val viewRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            zoomMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER)
            imageMatrix = zoomMatrix
            initStatus = true
            val m = imageMatrix
            val vals = FloatArray(9)
            m.getValues(vals)
            for (i in vals.indices) {
                if (i == 0) init_scale = vals[0]
                break
            }
        }

        super.onDraw(canvas)
    }

    fun resetImage() {
        if (drawable != null) {
            val drawableRect =
                RectF(
                    0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight
                        .toFloat()
                )
            val viewRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            zoomMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER)
            imageMatrix = zoomMatrix
            setInitStatus(false)
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        val s = x * x + y * y
        return Math.sqrt(s.toDouble()).toFloat()
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return if (gestureDetector!!.onTouchEvent(event)) {
            // single tap
            Log.e("aaa", "Clicked")
            if (zoomViewClick != null) zoomViewClick!!.onClicked()
            false
        } else {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(zoomMatrix)
                    start[event.x] = event.y
                    mode = DRAG
                    lastEvent = null
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(event)
                    if (oldDist > 10f) {
                        savedMatrix.set(zoomMatrix)
                        midPoint(mid, event)
                        mode = ZOOM
                    }
                    lastEvent = FloatArray(4)
                    lastEvent!![0] = event.getX(0)
                    lastEvent!![1] = event.getX(1)
                    lastEvent!![2] = event.getY(0)
                    lastEvent!![3] = event.getY(1)
                    d = rotation(event)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                    lastEvent = null
                }
                MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                    zoomMatrix.set(savedMatrix)
                    val dx = event.x - start.x
                    val dy = event.y - start.y
                    zoomMatrix.postTranslate(dx, dy)
                } else if (mode == ZOOM) {
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        zoomMatrix.set(savedMatrix)
                        val scale = newDist / oldDist
                        zoomMatrix.postScale(scale, scale, mid.x, mid.y)
                    }
                    if (lastEvent != null && event.pointerCount == 2 || event.pointerCount == 3) {
                        newRot = rotation(event)
                        val r = newRot - d
                        val values = FloatArray(9)
                        zoomMatrix.getValues(values)
                        val tx = values[2]
                        val ty = values[5]
                        val sx = values[0]
                        val xc = this.width / 2 * sx
                        val yc = this.height / 2 * sx
                        if (rotation) zoomMatrix.postRotate(r, tx + xc, ty + yc)
                    }
                }
            }
            imageMatrix = zoomMatrix
            val vals = FloatArray(9)
            zoomMatrix.getValues(vals)
            for (i in vals.indices) {
                if (!scaledown && i == 0 && vals[i] < init_scale) {
                    val drawableRect = RectF(
                        0f,
                        0f,
                        drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat()
                    )
                    val viewRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
                    zoomMatrix.setRectToRect(
                        drawableRect,
                        viewRect,
                        Matrix.ScaleToFit.CENTER
                    )
                    imageMatrix = zoomMatrix
                }
                break
            }
            invalidate()
            true
        }
    }

    private inner class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    interface ZoomViewClick {
        fun onClicked()
    }

    companion object {
        // For logging purposes
        private val TAG = ZoomImageView::class.java.simpleName

        // we can be in one of these 3 states
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }

}