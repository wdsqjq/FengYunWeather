package me.wsj.lib.specialeffects

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import per.wsj.commonlib.utils.LogUtil

/**
 * create by shiju.wang
 * sun
 */
class EffectSunDrawable(private val mSunDrawable: Drawable) : Drawable(), Animatable {

    private val mCenterPoint = PointF()

    /**
     * half width of drawable
     */
    private var halfWidth = 0

    private var currentAngel = 0f

    private var currentAlpha = 0

    private val mAnimatorSet = AnimatorSet()

    /*constructor(drawable: Drawable) : this() {

        startAnim()
    }*/
    init {
        startAnim()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        val width = bounds.right - bounds.left

        mCenterPoint.set(width * 7 / 8f, width * 3 / 16f)
        halfWidth = width * 7 / 8
    }

    private fun startAnim() {
        val rotateAnimator = ValueAnimator.ofFloat(0f, 360f)
        rotateAnimator.repeatCount = ValueAnimator.INFINITE
        rotateAnimator.duration = 15000
        rotateAnimator.addUpdateListener {
            currentAngel = (it.animatedValue) as Float
//                LogUtil.e("currentAngel: $currentAngel")
            invalidateSelf()
        }
        val alphaAnimator = ValueAnimator.ofInt(10, 255)
        alphaAnimator.repeatMode = ValueAnimator.REVERSE
        alphaAnimator.repeatCount = ValueAnimator.INFINITE
        alphaAnimator.duration = 5000
        alphaAnimator.addUpdateListener {
            currentAlpha = (it.animatedValue) as Int
        }
        mAnimatorSet.playTogether(rotateAnimator, alphaAnimator)
        mAnimatorSet.interpolator = LinearInterpolator()
        mAnimatorSet.start()
    }

    override fun draw(canvas: Canvas) {
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

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun start() {
        //To do sth.
    }

    override fun stop() {
        mAnimatorSet.cancel()
        LogUtil.d("Effect1Drawable cancel ---------------------------> ")
    }

    override fun isRunning(): Boolean {
        return mAnimatorSet.isRunning
    }
}