package me.wsj.lib.specialeffects

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateInterpolator
import me.wsj.lib.logdb.LogRepo
import me.wsj.lib.specialeffects.entity.Rain
import per.wsj.commonlib.utils.LogUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * create by shiju.wang
 * Rain
 */
class EffectSnowDrawable(val type: Int, val rains: Array<Drawable>) : Drawable(), Animatable {

    var rainScale = 60

    var speed = 10

    val random = Random()

    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var animator: ValueAnimator? = null

    val rainList = ArrayList<Rain>()

    private var mWidth = 0
    private var mHeight = 0

    init {
        paint.color = Color.WHITE
        when (type) {
            0 -> {
                rainScale = 50
                speed = 4
            }
            1 -> {
                rainScale = 65
                speed = 6
            }
            2 -> {
                rainScale = 80
                speed = 8
            }
        }

        initAnimator()
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.duration = 2000
        animator?.repeatCount = -1
        animator?.interpolator = AccelerateInterpolator()
        animator?.addUpdateListener {
            updatePosition()
        }
    }

    private fun updatePosition() {
        rainList.forEach {
            it.y += it.speed
            val drawable = rains[it.type]
            if (it.y > mHeight + drawable.intrinsicHeight) {
                it.y = 0f
                it.speed = random.nextInt(speed + 1) + speed
                it.type = random.nextInt(rains.size)
            }
        }
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mWidth = bounds.right - bounds.left
        mHeight = bounds.bottom - bounds.top
        for (i in 0 until rainScale) {
            val nextX = random.nextInt(mWidth)
            val nextY = random.nextInt(mHeight)
            rainList.add(
                Rain(
                    nextX.toFloat(), nextY.toFloat(), random.nextInt(speed + 1) + speed,
                    random.nextInt(rains.size)
                )
            )
        }
        LogRepo.getInstance().addLog("animator.start()")
        animator?.start()
    }

    override fun draw(canvas: Canvas) {
        for (rain in rainList) {
//            canvas.drawCircle(rain.x, rain.y, rain.radius, paint)
            val drawable = rains[rain.type]
            drawable.setBounds(
                (rain.x - drawable.intrinsicWidth / 2).toInt(),
                (rain.y - drawable.intrinsicHeight).toInt(),
                (rain.x + drawable.intrinsicWidth / 2).toInt(),
                rain.y.toInt()
            )

            drawable.draw(canvas)
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
        /*LogRepo.getInstance().addLog(
            "rain start() isRunning: " + animator?.isRunning
                    + " isPaused: " + animator?.isPaused + " isStarted: " + animator?.isStarted
        )
        animator?.let {
            if (it.isRunning && it.isStarted) {
                it.cancel()
                LogRepo.getInstance().addLog("rain start() not running")
                initAnimator()
                animator?.start()
            }
        }*/
    }

    override fun stop() {
        animator?.removeAllListeners()
        animator?.cancel()
        LogUtil.d("Effect Rain Drawable cancel ---------------------------> ")
    }

    override fun isRunning(): Boolean {
        return animator?.isRunning ?: false
    }
}