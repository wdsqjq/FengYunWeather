package me.wsj.fengyun.view.horizonview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import per.wsj.commonlib.utils.LogUtil;


/**
 *
 */
public class IndexHorizontalScrollView extends HorizontalScrollView {

    private HourlyForecastView hourlyForecastView;

    // 记录上次的offset, 防止父子互相刷新
    int lastOffset = -1;

    public IndexHorizontalScrollView(Context context) {
        this(context, null);
    }

    public IndexHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (hourlyForecastView != null) {
            ((ScrollWatcher) hourlyForecastView).update(l);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new IllegalStateException("require one child");
        }
        this.hourlyForecastView = (HourlyForecastView) getChildAt(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int offset = computeHorizontalScrollOffset();
        int range = computeHorizontalScrollRange();
        int maxOffset = range - getMeasuredWidth();
        if (hourlyForecastView != null && lastOffset != offset && maxOffset != 0) {
            lastOffset = offset;
            hourlyForecastView.setScrollOffset(offset, maxOffset);
        }
    }
}
