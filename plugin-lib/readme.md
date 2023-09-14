xml中配置了`onClick`标签需要特殊处理

```java
public final View createView(View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    View view = createViewFromHackInflater(context, name, attrs);

    if (view == null) {
    	view = createViewFromInflater(context, name, attrs);
    }

    if (view == null) {
    	view = createViewFromTag(context, name, attrs);
    }

    if (view != null) {
        // If we have created a view, check it's android:onClick
        checkOnClickListener(view, attrs);
    }

    return view;
}
```

添加`OnClickListener`

```java
private void checkOnClickListener(View view, AttributeSet attrs) {
    final Context context = view.getContext();

    if (!(context instanceof ContextWrapper) ||
        (Build.VERSION.SDK_INT >= 15 && !ViewCompat.hasOnClickListeners(view))) {
        // Skip our compat functionality if: the Context isn't a ContextWrapper, or
        // the view doesn't have an OnClickListener (we can only rely on this on API 15+ so
        // always use our compat code on older devices)
        return;
    }

    final TypedArray a = context.obtainStyledAttributes(attrs, sOnClickAttrs);
    final String handlerName = a.getString(0);
    if (handlerName != null) {
    	view.setOnClickListener(new DeclaredOnClickListener(view, handlerName));
    }
    a.recycle();
}
```

