package me.wsj.fengyun.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import me.wsj.fengyun.R
import me.wsj.lib.utils.WeatherUtil
import per.wsj.commonlib.utils.DisplayUtil

class AirConditionView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 底部偏移量
    var bottomOffset = 0f

    // 0，500的x坐标
    var x0 = 0f
    var x500 = 0f

    var mStrokeWidth = 0
    var mWidth = 0
    var centerY = 0f
    var bound = RectF()

    var mAngle = 0f
    var mAqi = 0
    var mCondition = ""

    var numX = 0f
    var numY = 0f
    var textX = 0f
    var textY = 0f


    val bgPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#20000000")
            mStrokeWidth = DisplayUtil.dp2px(5f)
            strokeWidth = mStrokeWidth.toFloat()
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
    }
    val progressPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = resources.getColor(R.color.color_air_leaf_1)
            mStrokeWidth = DisplayUtil.dp2px(5f)
            strokeWidth = mStrokeWidth.toFloat()
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
    }
    val rangePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#66000000")
            textSize = DisplayUtil.sp2px(13f).toFloat()
        }
    }

    val numPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#99000000")
            textSize = DisplayUtil.sp2px(28f).toFloat()
        }
    }

    fun setValue(aqi: Int, condition: String) {
        mAqi = aqi
        mAngle = 300 * aqi / 500f
        mCondition = condition

        numX = -numPaint.measureText(aqi.toString()) / 2
        textX = -rangePaint.measureText(condition) / 2
        progressPaint.color = WeatherUtil.getAirColor(context, aqi.toString())

        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        bottomOffset = rangePaint.fontMetrics.run {
            (ascent - top) * 5
        }

        centerY = (measuredHeight - bottomOffset) / 2f
        bound.left = -centerY + mStrokeWidth / 2
        bound.top = -centerY + mStrokeWidth / 2
        bound.right = centerY - mStrokeWidth / 2
        bound.bottom = centerY - mStrokeWidth / 2

        numY = numPaint.fontMetrics.run {
            (descent - ascent) / 2
        }

        textY = rangePaint.fontMetrics.run {
            -(descent - ascent)
        }

        x0 = bound.left / 2 - rangePaint.measureText("0") / 2
        x500 = bound.right / 2 - rangePaint.measureText("500") / 2
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(mWidth / 2f, centerY)

//        canvas.drawPoint(bound.left / 2, centerY, bgPaint)

        canvas.drawText("0", x0, centerY + bottomOffset, rangePaint)
        canvas.drawText("500", x500, centerY + bottomOffset, rangePaint)
        canvas.drawArc(bound, 120f, 300f, false, bgPaint)
        if (mAngle != 0f) {
            canvas.drawArc(bound, 120f, mAngle, false, progressPaint)
        }
        if (mCondition.isNotEmpty()) {
            canvas.drawText(mCondition, textX, textY, rangePaint)
        }

        if (mAqi != 0) {
            canvas.drawText(mAqi.toString(), numX, numY, numPaint)
        }
    }
}