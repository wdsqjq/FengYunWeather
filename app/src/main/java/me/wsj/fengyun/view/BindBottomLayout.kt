package me.wsj.fengyun.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.wsj.fengyun.utils.ContentUtil

class BindBottomLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mTotalLength = 0
    private var mExtentHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTotalLength = 0
        var top3Height = 0
        var maxWidth = 0

        // See how tall everyone is. Also remember max width.
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            // Determine how big this child would like to be. If this or
            // previous children have given a weight, then we allow it to
            // use all available space (and we will shrink things later
            // if needed).
            val usedHeight = mTotalLength
            measureChildWithMargins(
                child, widthMeasureSpec, 0,
                heightMeasureSpec, usedHeight
            )
            val childHeight = child.measuredHeight
            val lp = child.layoutParams as LayoutParams
            mTotalLength += childHeight + lp.topMargin + lp.bottomMargin
            val margin = lp.leftMargin + lp.rightMargin
            val measuredWidth = child.measuredWidth + margin
            maxWidth = Math.max(maxWidth, measuredWidth)
            if (i < 4) {
                top3Height += childHeight + lp.topMargin + lp.bottomMargin
            }
        }

        // Add in our padding
        mTotalLength += paddingTop + paddingBottom

        // 计算扩展高度，以保持前两个item保持在屏幕底部
//        mExtentHeight = (parent as ViewGroup).height - top3Height
        mExtentHeight = ContentUtil.visibleHeight - top3Height
        if (mExtentHeight > 0) {
            mTotalLength += mExtentHeight
        }
        maxWidth += paddingLeft + paddingRight
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(mTotalLength, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        super.onLayout(changed, l, t, r, b);
        val count = childCount
        var currentY = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            currentY += lp.topMargin
            child.layout(
                lp.leftMargin,
                currentY,
                lp.leftMargin + child.measuredWidth,
                currentY + child.measuredHeight
            )
            currentY += child.measuredHeight + lp.bottomMargin
            if (i == 0 && mExtentHeight > 0) {
//                LogUtil.e("mExtentHeight: " + mExtentHeight);
                currentY += mExtentHeight
            }
        }
    }
}