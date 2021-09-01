package me.wsj.fengyun.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import me.wsj.fengyun.adapter.MyItemTouchCallback

class MyRecyclerView : RecyclerView {
    private var itemTouchCallback: MyItemTouchCallback? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // close menu while it's open
        if (ev.action == MotionEvent.ACTION_DOWN) {
//            itemTouchCallback!!.forceClose(ev.x, ev.y)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setItemTouchCallback(itemTouchCallback: MyItemTouchCallback) {
        this.itemTouchCallback = itemTouchCallback
    }
}