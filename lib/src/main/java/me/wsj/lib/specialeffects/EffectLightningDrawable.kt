package me.wsj.lib.specialeffects

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import kotlinx.coroutines.*
import per.wsj.commonlib.utils.LogUtil

/**
 * create by shiju.wang
 * lightning
 */
class EffectLightningDrawable(val lightning1: Drawable, val lightning2: Drawable) :
    Drawable(), Animatable {

    private val scope by lazy { CoroutineScope(Job() + Dispatchers.Main) }

    private var mWidth = 0

    private var size1 = Point()
    private var size2 = Point()

    private var show1 = false

    private var show2 = false

    init {
        scope.launch {
            while (scope.isActive) {
                withContext(Dispatchers.Default) {
                    delay(1500)
                }
                // show one
                show1 = true
                invalidateSelf()
                withContext(Dispatchers.Default) {
                    delay(400)
                }
                // dismiss one
                show1 = false
                invalidateSelf()

                withContext(Dispatchers.Default) {
                    delay(500)
                }
                // show two
                show2 = true
                invalidateSelf()
                withContext(Dispatchers.Default) {
                    delay(400)
                }
                // dismiss two
                show2 = false
                invalidateSelf()

                withContext(Dispatchers.Default) {
                    delay(5_000)
                }
            }
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        mWidth = bounds.right - bounds.left
        size1.x = (lightning1.intrinsicWidth * 1.5f).toInt()
        size1.y = (lightning1.intrinsicHeight * 1.5f).toInt()

        size2.x = (lightning2.intrinsicWidth * 1.5f).toInt()
        size2.y = (lightning2.intrinsicHeight * 1.5f).toInt()
    }

    override fun draw(canvas: Canvas) {
        if (show1) {
            lightning1.setBounds(
                mWidth / 2 - size1.x / 2,
                0,
                mWidth / 2 + size1.x / 2,
                size1.y
            )
            lightning1.draw(canvas)
        }

        if (show2) {
            lightning2.setBounds(
                (mWidth * 0.6).toInt() - size2.x / 2,
                0,
                (mWidth * 0.6).toInt() + size2.x / 2,
                size2.y
            )
            lightning2.draw(canvas)
        }
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
        scope.cancel()
        LogUtil.d("Effect5Drawable cancel ---------------------------> ")
    }

    override fun isRunning(): Boolean {
        return scope.isActive
    }
}