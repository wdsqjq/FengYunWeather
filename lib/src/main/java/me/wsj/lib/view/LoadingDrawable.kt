package me.wsj.lib.view

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import kotlinx.coroutines.*
import per.wsj.commonlib.utils.LogUtil

/**
 * create by shiju.wang
 * cloud
 */
class LoadingDrawable(val sun: Drawable, val cloud: Drawable) : Drawable(), Animatable {

    private var scope: CoroutineScope? = null

    private var centerWidth = 0

    private var currentAngel = 0f

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        centerWidth = (bounds.right - bounds.left) * 2 / 5

        sun.setBounds(
            -centerWidth, -centerWidth, centerWidth, centerWidth
        )
        cloud.setBounds(
            -centerWidth, 0, centerWidth * 3 / 2, centerWidth * 3 / 2
        )
    }

    private fun startAnim() {
        if (scope == null) {
            scope = CoroutineScope(Job() + Dispatchers.Main)
            scope?.launch {
                while (isActive) {
//                    LogUtil.e("running() --------> ")
                    withContext(Dispatchers.Default) {
                        delay(20)
                    }
                    updatePosition()
                }
            }
        }
    }

    private fun updatePosition() {
        currentAngel += 4
        if (currentAngel > 360) {
            currentAngel = 0f
        }

        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.translate(centerWidth.toFloat(), centerWidth.toFloat())
        canvas.save()
        canvas.rotate(currentAngel)

        sun.draw(canvas)

        canvas.restore()

        cloud.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
//        LogUtil.e("alpha: " + alpha)
//        this.mAlpha = alpha
//        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun setProgressRotation(rotation: Float) {
        currentAngel = rotation * 360
        invalidateSelf()
    }

    override fun start() {
        LogUtil.d("start() -------------------> ")
        startAnim()
    }

    override fun stop() {
        scope?.cancel()
        scope = null
        LogUtil.d("ICancelable cancel ---------------------------> ")
    }

    override fun isRunning(): Boolean {
        scope?.let {
            return it.isActive
        } ?: run {
            return false
        }
    }
}