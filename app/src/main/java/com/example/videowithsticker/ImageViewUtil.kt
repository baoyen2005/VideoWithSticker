package com.example.videowithsticker

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.sqrt

class ImageViewUtil : AppCompatImageView{
    var mBitmap :Bitmap? = null
    private var preMatrix = Matrix()
    private var saveMatrix = Matrix()
    private var NONE = 0
    private var MOVE = 1
    private  var ZOOM = 2
    private var mode = NONE


    private val start = PointF()
    private val mid = PointF()
    private var oldDistance = 1f
    private var oldRotation = 0f
    var lastEvent: FloatArray? = null
    val TAG = "yenn"
    var dx =0f
    var dy = 0f
    var scale  =0f
    var rot = 0f

    constructor(context: Context): super(context){
        mBitmap = decodeSampledBitmapFromResource(context.resources,R.drawable.sticker,100,100)
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet){
        mBitmap = decodeSampledBitmapFromResource(context.resources, R.drawable.sticker,100,100)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        mBitmap = decodeSampledBitmapFromResource(context.resources,R.drawable.sticker,100,100)
    }
    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    private fun decodeSampledBitmapFromResource(
        res: Resources?, resId: Int,
        reqWidth: Int, reqHeight: Int
    ): Bitmap? {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas!!.drawBitmap(mBitmap!!,preMatrix,null)
        canvas!!.drawColor(Color.TRANSPARENT)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN -> {
                saveMatrix.set(preMatrix)
                start[event.x] = event.y
                mode = MOVE
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = spacing(event)
                if(oldDistance > 10f){
                    saveMatrix.set(preMatrix)
                    Log.d("yenn", "ACTION_POINTER_DOWN: prematrix "+ preMatrix)
                    Log.d("yenn", "ACTION_POINTER_DOWN: prematrix "+ preMatrix)
                    midPoint(mid, event)
                   mode = ZOOM
                 }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                oldRotation = rotation(event)
            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> {
                if(mode === MOVE){
                    preMatrix.set(saveMatrix)
                    dx = event.x - start.x
                    dy = event.y - start.y
                    preMatrix.postTranslate(dx,dy)
                }
                else if (mode === ZOOM) {

                    val newDist: Float = spacing(event)
                    if (newDist > 10f) {
                        preMatrix.set(saveMatrix)
                        Log.e("yenn", "Action_move zoom: prematrix "+ preMatrix)
                        Log.e("yenn", "Action_move zoom: prematrix "+ saveMatrix)
                        scale = newDist / oldDistance
                        preMatrix.postScale(scale, scale, mid.x, mid.y)
                    }
                    if (lastEvent != null && event.pointerCount == 2) {
                        val newRotation = rotation(event)
                        rot = newRotation - oldRotation
                        preMatrix.postRotate(rot, mid.x, mid.y)
                        Log.e("yenn", "Action_move rotation: prematrix ")

                    }
                }
            }
            MotionEvent.ACTION_OUTSIDE->{
                return false
            }
        }
        invalidate()
        return true
    }
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }


    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }


    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }
}