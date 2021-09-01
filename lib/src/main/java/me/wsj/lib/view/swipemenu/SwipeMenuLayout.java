package me.wsj.lib.view.swipemenu;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import per.wsj.commonlib.utils.LogUtil;


public class SwipeMenuLayout extends ViewGroup {

    private int mTouchSlop;
    // content部分高度，也是整个itemview的高度
    private int mHeight;
    // 右侧菜单宽度总和(最大滑动距离)
    private int mMenuViewWidth;

    private Scroller mScroller;
    // 滑动速度变量
    private VelocityTracker mVelocityTracker;

    private PointF mFirstP = new PointF();

    // 存储的是当前正在展开的View
    private static SwipeMenuLayout mViewCache;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 返回ViewCache
     *
     * @return
     */
    public static SwipeMenuLayout getViewCache() {
        return mViewCache;
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //初始化滑动帮助类对象
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMenuViewWidth = 0;//由于ViewHolder的复用机制，每次这里要手动恢复初始值
        mHeight = 0;
        int contentWidth = 0;//2016 11 09 add,适配GridLayoutManager，将以第一个子Item(即ContentItem)的宽度为控件宽度
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                if (i == 0) {
                    // 测量ContentView
                    measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    contentWidth = childView.getMeasuredWidth();
                    mHeight = Math.max(mHeight, childView.getMeasuredHeight());
                } else {
                    // 测量menu
                    LayoutParams layoutParams = childView.getLayoutParams();
                    int widthSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
                    int heightSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
                    childView.measure(widthSpec, heightSpec);
                    mMenuViewWidth += childView.getMeasuredWidth();
                }
            }
        }
        //宽度取第一个Item(Content)的宽度
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth,
                mHeight + getPaddingTop() + getPaddingBottom());

        /*int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i > 0) {
                mMenuViewWidth += getChildAt(i).getMeasuredWidth();
            }
        }*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                left = left + childView.getMeasuredWidth();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        obtainVelocity(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果scroller还没有滑动结束 停止滑动动画
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = ev.getX();
                mFirstP.set(ev.getX(), ev.getY());
                //如果down，view和cacheview不一样，则立马让它还原。且把它置为null
                if (mViewCache != null) {
                    if (mViewCache != this) {
                        mViewCache.smoothClose();
                        return true;
                    }
                    // 只要有一个侧滑菜单处于打开状态， 就不给外层布局上下滑动了
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                float x = ev.getX();
                float y = ev.getY();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity)
                        || Math.abs(x - mFirstP.x) >= mTouchSlop
                        && Math.abs(x - mFirstP.x) > Math.abs(y - mFirstP.y)) {
                    LogUtil.e("拦截。。。。");
                    return true;
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                releaseVelocity();
                break;
            case MotionEvent.ACTION_UP:
                // 点击区域在展开的Itemview的contentView区域，则关闭Menu
                if (this == mViewCache) {
                    if (ev.getX() < getWidth() - getScrollX()) {
                        smoothClose();
                        return true;//true表示拦截
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float mLastX;   // 滑动过程中记录上次触碰点X

    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        obtainVelocity(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                getParent().requestDisallowInterceptTouchEvent(true);
                float dx = mLastX - x;
                if (getScrollX() + dx > 0 && getScrollX() + dx < mMenuViewWidth) {
                    scrollBy((int) dx, 0);
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int scrollX = getScrollX();
                if (mVelocityTracker.getXVelocity() < -SNAP_VELOCITY) {    // 向左侧滑达到侧滑最低速度，则打开
                    int delt = Math.abs(mMenuViewWidth - scrollX);
                    int t = (int) (delt / mVelocityTracker.getXVelocity() * 1000);
                    smoothExpand(t);
                } else if (mVelocityTracker.getXVelocity() >= SNAP_VELOCITY) {  // 向右侧滑达到侧滑最低速度，则关闭
                    smoothClose();
                } else if (scrollX >= mMenuViewWidth / 2) { // 如果超过删除按钮一半，则打开
                    smoothExpand(100);
                } else {    // 其他情况则关闭
                    smoothClose();
                }
                releaseVelocity();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void smoothExpand(int time) {
        // 展开就加入ViewCache：
        mViewCache = SwipeMenuLayout.this;
        mScroller.startScroll(getScrollX(), 0, mMenuViewWidth - getScrollX(), 0, time);
        invalidate();
    }


    /**
     * 平滑关闭
     */
    public void smoothClose() {
        mViewCache = null;
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 100);
        invalidate();
    }

    //平滑滚动 弃用 改属性动画实现
    @Override
    public void computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate();
        }
    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void obtainVelocity(MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * * 释放VelocityTracker
     *
     * @see VelocityTracker#clear()
     * @see VelocityTracker#recycle()
     */
    private void releaseVelocity() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    //每次ViewDetach的时候，判断一下 ViewCache是不是自己，如果是自己，关闭侧滑菜单，且ViewCache设置为null，
    // 理由：1 防止内存泄漏(ViewCache是一个静态变量)
    // 2 侧滑删除后自己后，这个View被Recycler回收，复用，下一个进入屏幕的View的状态应该是普通状态，而不是展开状态。
    @Override
    protected void onDetachedFromWindow() {
        if (this == mViewCache) {
            mViewCache.smoothClose();
            mViewCache = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * 快速关闭。
     * 用于 点击侧滑菜单上的选项,同时想让它快速关闭(删除 置顶)。
     * 这个方法在ListView里是必须调用的，
     * 在RecyclerView里，视情况而定，如果是mAdapter.notifyItemRemoved(pos)方法不用调用。
     */
    public void quickClose() {
        if (this == mViewCache) {
            mViewCache.scrollTo(0, 0);//关闭
            mViewCache = null;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        // measureChildWithMargins()需要用到
        return new MarginLayoutParams(getContext(), attrs);
    }
}
