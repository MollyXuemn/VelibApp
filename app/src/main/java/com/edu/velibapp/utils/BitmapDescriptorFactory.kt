package com.edu.velibapp.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * Created by Administrator on 2017/10/18.
 */
object BitmapDescriptorUtils {

    fun fromBitmap(bitmap: Bitmap?): Bitmap? {
        return if (bitmap == null) {
            null
        } else {
            val width = bitmap.width
            val height = bitmap.height
            val bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap1)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
            return bitmap1;
        }
    }



    fun fromView(view: View?): Bitmap? {
        return if (view == null) {
            null
        } else {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            val var1 = view.drawingCache
            val var2 = fromBitmap(var1)
            var1?.recycle()
            view.destroyDrawingCache()
            var2
        }
    }
}