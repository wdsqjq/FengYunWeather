package me.wsj.fengyun.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import me.wsj.fengyun.R
import me.wsj.fengyun.bean.Daily
import per.wsj.commonlib.utils.DisplayUtil

/**
 * create by shiju.wang
 */

class TempChart @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var topBottom = 0
    private var minTemp = 0
    private var maxTemp = 0
    private var lowTemp = 0
    private var highTemp = 0
    private var mHalfWidth = 0f
    private var mHeight = 0f
    private var mLowPaint: Paint
    private var mHighPaint: Paint
    private var mTextPaint: Paint
    private var textHeight = 0
    private var lowText = ""
    private var highText = ""
    private var lowTextWidth = 0
    private var highTextWidth = 0
    private var usableHeight = 0
    private var tempDiff = 0
    private var density = 0f
    private var pntRadius = 0f

    // 前一天数据
    private var mPrev: Daily? = null

    // 后一天数据
    private var mNext: Daily? = null


    init {
        topBottom = DisplayUtil.dp2px(8f)
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textSize = DisplayUtil.sp2px(context, 12f).toFloat()
        mTextPaint.color = resources.getColor(R.color.color_666)
        textHeight = (mTextPaint.fontMetrics.bottom - mTextPaint.fontMetrics.top).toInt()
        val lineWidth = DisplayUtil.dp2px(2f)
        mLowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLowPaint.strokeWidth = lineWidth.toFloat()
        // 设置线帽，方式折线陡峭时线中间出现裂痕
        mLowPaint.strokeCap = Paint.Cap.SQUARE
        mLowPaint.color = Color.parseColor("#00A368")
        mHighPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHighPaint.strokeWidth = lineWidth.toFloat()
        mHighPaint.strokeCap = Paint.Cap.SQUARE
        mHighPaint.color = Color.parseColor("#FF7200")
        pntRadius = DisplayUtil.dp2px(3f).toFloat()
    }


    fun setData(minTemp: Int, maxTemp: Int, prev: Daily?, current: Daily, next: Daily?) {
//        LogUtil.e("min: " + minTemp + " max:" + maxTemp + " prev:" + prev + " curr: " + current + " next: " + next);
        this.minTemp = minTemp
        this.maxTemp = maxTemp
        lowTemp = current.tempMin.toInt()
        highTemp = current.tempMax.toInt()
        mPrev = prev
        mNext = next
        lowText = "$lowTemp°C"
        highText = "$highTemp°C"
        lowTextWidth = mTextPaint.measureText(lowText).toInt()
        highTextWidth = mTextPaint.measureText(highText).toInt()
        tempDiff = maxTemp - minTemp
        if (usableHeight != 0) {
            density = usableHeight / tempDiff.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHalfWidth = measuredWidth / 2f
        mHeight = measuredHeight.toFloat()
        usableHeight = (mHeight - topBottom * 2 - textHeight * 2).toInt()
        density = usableHeight / tempDiff.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(mHalfWidth, 0f)
        val topY = ((maxTemp - highTemp) * density + topBottom + textHeight).toInt()
        val bottomY = ((maxTemp - lowTemp) * density + topBottom + textHeight).toInt()
        canvas.drawCircle(0f, topY.toFloat(), pntRadius, mHighPaint)
        canvas.drawCircle(0f, bottomY.toFloat(), pntRadius, mLowPaint)
        canvas.drawText(
            highText,
            (-lowTextWidth / 2).toFloat(),
            topY - mTextPaint.fontMetrics.bottom * 2,
            mTextPaint
        )
        canvas.drawText(
            lowText,
            (-lowTextWidth / 2).toFloat(),
            (bottomY + textHeight).toFloat(),
            mTextPaint
        )
        // 绘制当前点给前一天数据的连线
        if (mPrev != null) {
            val prev = getEnds(mPrev!!)
            canvas.drawLine(-mHalfWidth, (prev.first + topY) / 2f, 0f, topY.toFloat(), mHighPaint)
            canvas.drawLine(
                -mHalfWidth,
                (prev.second + bottomY) / 2f,
                0f,
                bottomY.toFloat(),
                mLowPaint
            )
        }
        // 绘制当前点给后一天数据的连线
        if (mNext != null) {
            val next = getEnds(mNext!!)
            canvas.drawLine(0f, topY.toFloat(), mHalfWidth, (next.first + topY) / 2f, mHighPaint)
            canvas.drawLine(
                0f,
                bottomY.toFloat(),
                mHalfWidth,
                (next.second + bottomY) / 2f,
                mLowPaint
            )
        }
    }

    private fun getEnds(daily: Daily): Pair<Int, Int> {
        val topY = ((maxTemp - daily.tempMax.toInt()) * density + topBottom + textHeight).toInt()
        val bottomY = ((maxTemp - daily.tempMin.toInt()) * density + topBottom + textHeight).toInt()
        return Pair(topY, bottomY)
    }
}