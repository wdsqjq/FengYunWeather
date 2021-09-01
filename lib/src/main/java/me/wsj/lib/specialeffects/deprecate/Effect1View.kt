package me.wsj.lib.specialeffects.deprecate

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import me.wsj.lib.R

/**
 * create by shiju.wang
 */
class Effect1View : View {

    lateinit var mSunDrawable: Drawable

    private val mCenterPoint = PointF()

    /**
     * half width of drawable
     */
    private var halfWidth = 0

    private var currentAngel = 0f

    private var currentAlpha = 0

    var animatorSet: AnimatorSet? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
//        val a = context.obtainStyledAttributes(attrs, R.styleable.MyView, defStyle, 0)
//        a.recycle()
        mSunDrawable = resources.getDrawable(R.drawable.sun_icon)

        startAnim()
    }

    private fun startAnim() {
        if (animatorSet == null) {
            val rotateAnimator = ValueAnimator.ofFloat(0f, 360f)
            rotateAnimator.repeatCount = ValueAnimator.INFINITE
            rotateAnimator.duration = 15000
            rotateAnimator.addUpdateListener {
                currentAngel = (it.animatedValue) as Float
                invalidate()
            }
            val alphaAnimator = ValueAnimator.ofInt(10, 255)
            alphaAnimator.repeatMode = ValueAnimator.REVERSE
            alphaAnimator.repeatCount = ValueAnimator.INFINITE
            alphaAnimator.duration = 5000
            alphaAnimator.addUpdateListener {
                currentAlpha = (it.animatedValue) as Int
            }
            animatorSet = AnimatorSet()
            animatorSet?.playTogether(rotateAnimator, alphaAnimator)
            animatorSet?.interpolator = LinearInterpolator()
            animatorSet?.start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mCenterPoint.set(measuredWidth * 7 / 8f, measuredWidth * 3 / 16f)
        halfWidth = measuredWidth * 7 / 8
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // move to center point
        canvas.translate(mCenterPoint.x, mCenterPoint.y)
        // rotate canvas
        canvas.rotate(currentAngel)
        // set alpha
        mSunDrawable.mutate().alpha = currentAlpha
        // set drawable bounds & draw it
        mSunDrawable.setBounds(-halfWidth, -halfWidth, halfWidth, halfWidth)
        mSunDrawable.draw(canvas)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet?.cancel()
    }
}