package me.wsj.lib.specialeffects

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import per.wsj.commonlib.utils.LogUtil

/**
 * create by shiju.wang
 * moon
 */
class EffectMoonNDrawable(private val mMoonDrawable: Drawable) : Drawable(), Animatable {

    private val mCenterPoint = PointF()

    /**
     * half width of drawable
     */
    private var halfWidth = 0


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        val width = bounds.right - bounds.left

        mCenterPoint.set(width * 5 / 6f, width * 5 / 16f)
        halfWidth = width / 5
    }

    override fun draw(canvas: Canvas) {
        canvas.translate(mCenterPoint.x, mCenterPoint.y)
        mMoonDrawable.setBounds(-halfWidth, -halfWidth, halfWidth, halfWidth)
        mMoonDrawable.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun start() {

    }

    override fun stop() {
        LogUtil.d("Effect1NDrawable cancel ---------------------------> ")
    }

    override fun isRunning(): Boolean {
        return true
    }
}