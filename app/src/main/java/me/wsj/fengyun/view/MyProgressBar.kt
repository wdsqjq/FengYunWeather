package me.wsj.fengyun.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import me.wsj.fengyun.R
import per.wsj.commonlib.utils.DisplayUtil

class MyProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 右侧未完成进度条的颜色
     */
    private val paintStartColor = -0x1a1a1b

    /**
     * 得到自定义视图的宽度
     */
    private var viewWidth = 0

    /**
     * 得到自定义视图的Y轴中心点
     */
    private var viewCenterY = 0

    /**
     * 已完成的画笔
     */
    private val paintInit = Paint()
    private var rectFinish = RectF()
    private var rectGray = RectF()
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 未完成进度条画笔的属性
     */
    private val paintStart = Paint()

    // 大圆半径
    private var bigR: Int
    private var radius: Float

    // 气泡矩形
    private var jR: Int

    /**
     * 文字总共移动的长度（即从0%到100%文字左侧移动的长度）
     */
    init {
        // 构造器中初始化数据
        bigR = DisplayUtil.dp2px(8f) //大圆半径
        radius = DisplayUtil.dp2px(5f).toFloat() //进度条高度
        jR = DisplayUtil.dp2px(4f) //矩形
        initData()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        // 未完成进度条画笔的属性
        paintStart.color = paintStartColor
        paintStart.strokeWidth = DisplayUtil.dp2px(1f).toFloat()
        paintStart.isDither = true
        paintStart.isAntiAlias = true
        paintStart.style = Paint.Style.FILL

        // 已完成进度条画笔的属性
        paintInit.color = context.resources.getColor(R.color.colorPrimaryDark)
        paintInit.strokeWidth = DisplayUtil.dp2px(1f).toFloat()
        paintInit.isAntiAlias = true
        paintInit.isDither = true
        paintInit.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        setProgress(0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 灰色背景
        canvas.drawRoundRect(rectFinish, radius, radius, paintStart)

        // 当前进度
        canvas.drawRoundRect(rectGray, radius, radius, paintInit)
    }

    /**
     * @param progress 外部传进来的当前进度
     */
    fun setProgress(progress: Int) {
        //得到float型进度
        val progressFloat = progress / 100.0f
        viewWidth = mWidth - 4 * jR
        viewCenterY = mHeight - bigR
        val currentMovedLen = viewWidth * progressFloat + 2 * jR

//        new RectF(2 * jR, viewCenterY - radius, viewWidth + 2 * jR,
//                viewCenterY + radius);
        rectFinish.left = (2 * jR).toFloat()
        rectFinish.top = viewCenterY - radius
        rectFinish.right = (viewWidth + 2 * jR).toFloat()
        rectFinish.bottom = viewCenterY + radius
        rectGray.left = (2 * jR).toFloat()
        rectGray.top = viewCenterY - radius
        rectGray.right = currentMovedLen
        rectGray.bottom = viewCenterY + radius
        invalidate()
    }
}