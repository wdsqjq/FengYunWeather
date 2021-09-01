## 总结一以下Android侧滑删除的两种实现

侧滑删除是App中常见的一个功能，理解了它的原理你就对自定义ViewGroup的测量、摆放及触摸事件的处理有更深的认识。

为什么有两种实现呢？这个功能可以从不同的角度来实现：

- 一种是父布局来处理、分发事件，控制子view的位置，也就是通过自定义RecyclerView实现
- 另一种是通过子ViewGroup拦截事件，处理事件来实现，也就是自定义ItemView的布局

两种方式总体思路都是一样的，`content` 占满屏幕，菜单View 在屏幕之外，当滑动的时候，`content`滑屏幕，menu 进入屏幕，就达到了需要的效果，布局草图如下：

![mock](\mock.png)

### 一，自定义RecyclerView

自定义RecyclerView方式有三个关键点：

- 根据触摸点找到触摸的ItemView
- 何时拦截事件
- 如何让隐藏的Menu滑出

#### 1.1，根据触摸点找到触摸的ItemView

首先RecyclerView是通过回收、复用ItemView来避免创建大量对象，提高性能的，因此它内部的子view也就是一屏中可以看到的那些ItemView，可以通过RecyclerView遍历查找到所有ItemView，根据ItemView可以获取其`Bound`，也就是一个`Rect`，有了这个`Rect`，就可以判断触摸点是不是在这个ItemView中，也就能找到触摸点所在的ItemView。代码如下：

```java
Rect frame = new Rect();

final int count = getChildCount();
for (int i = count - 1; i >= 0; i--) {
	final View child = getChildAt(i);
	if (child.getVisibility() == View.VISIBLE) {
		// 获取子view的bound
		child.getHitRect(frame);
        // 判断触摸点是否在子view中
        if (frame.contains(x, y)) {
            return i;
        }
	}
}
```

#### 1.2，何时拦截事件

RecyclerView需要处理手势事件，内部的ItemVIew也需要处理事件，那在何时去拦截事件呢？分以下两种情况：

- ACTION_DOWN时，如果已经有ItemView处于展开状态，并且这次点击的对象不是已打开的那个ItemView，则拦截事件，并将已展开的ItemView关闭。

- ACTION_MOVE时，有俩判断，满足其一则认为是侧滑：1. x方向速度大于y方向速度，且大于最小速度限制；2. x方向的侧滑距离大于y方向滑动距离，且x方向达到最小滑动距离；

代码如下：

```java
public class SwipeDeleteRecyclerView extends RecyclerView {
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        ...
        switch (e.getAction()) {
            // 第一种情况
            case MotionEvent.ACTION_DOWN:
                ...
                // 已经有ItemView处于展开状态，并且这次点击的对象不是已打开的那个ItemView
                if (view != null && mFlingView != view && view.getScrollX() != 0) {
                    // 将已展开的ItemView关闭
                    view.scrollTo(0, 0);
                    // 则拦截事件
                    return true;
                }
             	break;
             // 第二种情况
             case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                // 此处有俩判断，满足其一则认为是侧滑：
                // 1.如果x方向速度大于y方向速度，且大于最小速度限制；
                // 2.如果x方向的侧滑距离大于y方向滑动距离，且x方向达到最小滑动距离；
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity)
                        || Math.abs(x - mFirstX) >= mTouchSlop
                        && Math.abs(x - mFirstX) > Math.abs(y - mFirstY)) {

                    mIsSlide = true;
                    return true;
                }
                break;
                ...
        }
        ...
    }
}
```

拦截了事件以后就该处理事件了，接着往下看。



#### 1.3，让隐藏的Menu滑出

接着在`onTouchEvent`中处理事件，让隐藏的Menu滑出。

- 首先是在`ACTION_MOVE`中，如果处于侧滑状态则让目标ItemView通过`scrollBy()`跟着手势移动，注意判断边界

- 在`ACTION_UP`中，此时会产生两个结果：一个是继续展开菜单，另一个是关闭菜单。这两个结果又都分了两种情况：

  1，当松手时向左的滑动速度超过了阈值，就让目标ItemView保持松手时的速度继续展开。

  2，当松手时向右的滑动速度超过了阈值，就让目标ItemView关闭。

  3，当松手时移动的距离超过了隐藏的宽度的一半(也就是最大可以移动的距离的一半)，则让ItemVIew继续展开。

  4，当松手时移动的距离小于隐藏的宽度的一半，则让ItemVIew关闭。

```java
public boolean onTouchEvent(MotionEvent e) {
	obtainVelocity(e);
	switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = mLastX - x;
			// 判断边界
			if (mFlingView.getScrollX() + dx <= mMenuViewWidth
					&& mFlingView.getScrollX() + dx > 0) {
				// 随手指滑动
				mFlingView.scrollBy((int) dx, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			int scrollX = mFlingView.getScrollX();
			mVelocityTracker.computeCurrentVelocity(1000);
			
			if (mVelocityTracker.getXVelocity() < -SNAP_VELOCITY) {    // 向左侧滑达到侧滑最低速度，则打开
				// 计算剩余要移动的距离
				int delt = Math.abs(mMenuViewWidth - scrollX);
				// 根据松手时的速度计算要移动的时间
				int t = (int) (delt / mVelocityTracker.getXVelocity() * 1000);
				// 移动
				mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(t));
			} else if (mVelocityTracker.getXVelocity() >= SNAP_VELOCITY) {  // 向右侧滑达到侧滑最低速度，则关闭
				mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
			} else if (scrollX >= mMenuViewWidth / 2) { // 如果超过删除按钮一半，则打开
				mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(mMenuViewWidth - scrollX));
			} else {    // 其他情况则关闭
				mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
			}
			invalidate();
			releaseVelocity();  // 释放追踪
			break;
	}
	return true;
}
```

这里通过`VelocityTracker`来获取滑动速度，通过`Scroller`来控制ItemView滑动。

#### 1.4，删除Item

在RecyclerView的Holder的`onBindViewHolder()`中给滑出来的菜单添加点击事件即可响应删除

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {

	holder.tvDelete.setOnClickListener {
		onDelete(holder.adapterPosition)
	}
}
```

由于RecyclerView的复用机制，需要在点了删除菜单删除Item后，让Item关闭，不然就会出现删除一个Item后往下滚动，会再出来一个已展开的Item。

```kotlin
fun onDelete(it:Int){
	mData.removeAt(it)
	adapter.notifyItemRemoved(it)
    // 调用closeMenu()关闭该item
	mBinding.rvAll.closeMenu()
}
```

关闭的方法很简单，只需要让该Item `scrollTo(0, 0)`即可

```java
public void closeMenu() {
    if (mFlingView != null && mFlingView.getScrollX() != 0) {
        // 关闭
        mFlingView.scrollTo(0, 0);
    }
}
```





#### 1.5，最后

**注意：当向左的滑动速度超过阈值时要让目标ItemView保持松手时的速度继续展开，此时需要先计算出剩余要滑动的距离，然后根据松手时的速度计算出剩余滑动的时间，作为Scroller.startScroll()的时间。否则可能会出现卡顿的情况。**

最后看一下效果：

![mock](\linear.gif) ![mock](\grid.gif)



### 二，自定义ItemView

自定义ItemView方式和自定义RecyclerView方式总体思路是一致的，不同点有：

- 自定义ItemView继承自ViewGroup
- 自定义ItemView需要对子view进行测量摆放
- 自定义ItemView不仅需要拦截向下拦截事件（拦截子View的事件），还需要向上拦截，也就是拦截父View的事件



#### 2.1，测量布局

测量过程比较简单，要将contentView和menuView分开测量。contentView直接使用`measureChildWithMargins()`测量，测量的高度作为整个item的高度，因此menuView的高度也要跟随其高度。menuView测量时需要构造其对应的`widthMeasureSpec`和`widthMeasureSpec`，将所有menuView的宽度累加为`mMenuViewWidth`后面要用到。

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// 隐藏的菜单的宽度
	mMenuViewWidth = 0;
    // content部分的高度
	mHeight = 0;
    // content部分的高度
	int contentWidth = 0;
    
	int childCount = getChildCount();
	for (int i = 0; i < childCount; i++) {
		View childView = getChildAt(i);
		if (i == 0) {
			// 测量ContentView
			measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
			contentWidth = childView.getMeasuredWidth();
			mHeight = Math.max(mHeight, childView.getMeasuredHeight());
		} else {
			// 测量menu
			LayoutParams layoutParams = childView.getLayoutParams();
			int widthSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            // mHeight作为其精确高度
			int heightSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
			childView.measure(widthSpec, heightSpec);
			mMenuViewWidth += childView.getMeasuredWidth();
		}
	}
	// 宽度取第一个Item(Content)的宽度
	setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth,
			mHeight + getPaddingTop() + getPaddingBottom());
}
```

#### 2.2，摆放布局

由于测量过程中已经确定了所有子view的宽高，因此直接摆放子view即可。

```java
@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int childCount = getChildCount();
    int left = getPaddingLeft();
    for (int i = 0; i < childCount; i++) {
        View childView = getChildAt(i);
        childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
        left = left + childView.getMeasuredWidth();
    }
}
```

#### 2.3，拦截事件

自定义ItemView实现方式拦截事件有两方面：

1，在`onInterceptTouchEvent()`中`return true`来实现拦截

2，通过`getParent().requestDisallowInterceptTouchEvent(true);`阻止父view拦截事件

那么哪些情况需要拦截呢？其实和自定义RecyclerView方式差不多，分两种情况：

- ACTION_DOWN时，如果已经有ItemView处于展开状态，并且这次点击的对象不是已打开的那个ItemView，则拦截事件，并将已展开的ItemView关闭。

- ACTION_MOVE时，有俩判断，满足其一则认为是侧滑：1. x方向速度大于y方向速度，且大于最小速度限制；2. x方向的侧滑距离大于y方向滑动距离，且x方向达到最小滑动距离；

和自定义RecyclerView方式不同的是，自定义RecyclerView中可以持有已打开的ItemView的引用。而自定义ItemView中需要通过经常变量来保存已打开的ItemView。这里就不放代码了。参看：[SwipeMenuLayout](https://github.com/wsj1024/FengYunWeather/blob/master/lib/src/main/java/me/wsj/lib/view/swipemenu/SwipeMenuLayout.java)

#### 2.4，消费事件

所谓消费事件也就是在`onTouchEvent`中对事件进行处理，实现侧滑效果。实现思路也和自定义RecyclerView方式基本一致，这里不多说了。



#### 2.5，删除Item

删除也是通过给menuView添加点击事件实现，和自定义RecyclerView方式不同之处在于不需要手动调用关闭该ItemView的操作。只需要在自定义ItemView的`onDetachedFromWindow`关闭并销毁即可。代码如下：

```java
@Override
protected void onDetachedFromWindow() {
    if (this == mViewCache) {
        mViewCache.smoothClose();
        mViewCache = null;
    }
    super.onDetachedFromWindow();
}
```

#### 2.6，局限

该方式存在一个局限就是通过`holder.itemView`添加的点击事件无效，需要给其中的contentView添加点击事件。

```kotlin
// 给itemView设置点击事件无效
holder.itemView.setOnClickListener {
    onClick(item)
}
// 给content设置点击事件
holder.itemContent.setOnClickListener {
    onClick(item)
}
```



### 三，总结

##### 1，共同点

两种方式的总体思路都是一样的：

1. 布局

   布局中的content部分宽度占据整个ItemView的宽度，菜单部分隐藏在content部分的右侧。

2. 事件拦截

   发生在`onInterceptTouchEvent`中

   - `ACTION_DOWN`时，判断是否有打开的菜单，如果有并且不是当前事件所在的Item，则拦截事件，并关闭菜单。

   - `ACTION_MOVE`时，如果x方向的速度大于速度阈值并且大于y方向速度则或x方向移动距离大于距离阈值并且大于y方向移动的距离则拦截事件。

3. 事件响应

   发生在`onTouchEvent`中

   - `ACTION_MOVE`时，通过`scrollBy()`让当前ItemView随着手指移动，注意判断边界。

   - `ACTION_UP`时，如果向左滑动的速度大于阈值，并没菜单没有完全打开，则通过`scroller`让其打开。需要根据速度及剩余距离计算展开需要的时间。
   - 同上当向右滑动的速度大于阈值，并没菜单没有完全关闭，则通过`scroller`让其关闭。

   - `ACTION_UP`时，如果滑动速度小于阈值，并且滑动距离超过menu部分宽度的一半，则通过`scroller`让其打开；如果滑动距离小于menu部分宽度的一半则关闭。

##### 2，不同点

- 自定义RecyclerView需要根据触摸点的位置找到对应的itemView，并将展开的itemView对象保存其中；

自定义ItemView只需通过静态变量保存当前打开的itemView对象即可。

- 自定义RecyclerView在触发删除时需要在业务层手动关闭当前的itemView菜单。自定义ItemView可以自动关闭。
- 自定义RecyclerView可以通过xml实现布局。自定义ItemView需要自己测量摆放子view。

##### 3，侵入性

​	两种方式都需要通过xml引用，但是自定义RecyclerView方式在触摸删除时需要手动关闭menu，侵入性高于自定义ItemView。

##### 4，注意点

​	在手指快速滑动时需要根据手指抬起时的速度，以及剩余要滑动的距离来计算出scroller要滑动的时间，这样就保证了自由滑动的速度和送手时的速度一致。可以避免卡顿的情况。



最后代码在这里：[自定义RecyclerView](https://github.com/wsj1024/FengYunWeather/blob/master/lib/src/main/java/me/wsj/lib/view/SwipeDeleteRecyclerView.java)，[自定义ItemView](https://github.com/wsj1024/FengYunWeather/blob/master/lib/src/main/java/me/wsj/lib/view/swipemenu/SwipeMenuLayout.java)



参考：

https://blog.csdn.net/dapangzao/article/details/80524774

