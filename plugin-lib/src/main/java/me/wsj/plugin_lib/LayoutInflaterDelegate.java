package me.wsj.plugin_lib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import per.wsj.commonlib.utils.LogUtil;


/**
 * 用来接管系统的view的生产过程
 */
public class LayoutInflaterDelegate implements LayoutInflater.Factory2, Observer {

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private SkinAttribute skinAttribute;
    // 用于获取窗口的状态框的信息
    private Activity activity;

    private LayoutInflater.Factory originFactory;

    public LayoutInflaterDelegate(Activity activity, LayoutInflater.Factory origin) {
        this.activity = activity;
        this.originFactory = origin;
        skinAttribute = new SkinAttribute();
    }

    private List<String> viewNames = Arrays.asList(
            "me.wsj.fengyun.view.plugin.PluginImageView",
            "me.wsj.fengyun.view.horizonview.HourlyForecastView");

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // 1, 通过Factory1创建view
        View view = originFactory.onCreateView(name, context, attrs);
        // 2, 创建自定义view
        if (view == null && viewNames.contains(name)) {
            view = createView(name, context, attrs);
        }
        // 3, 加入自己的逻辑
        if (null != view) {
            // 加载属性
            skinAttribute.load(view, attrs);
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private static final HashMap<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();

    // 记录对应View的构造函数
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass
                        (name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        return constructor;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        return null;
        return originFactory.onCreateView(name, context, attrs);
    }

    // 接收更新通知
    @Override
    public void update(Observable o, Object arg) {
        LogUtil.d("factory update");
//        SkinThemeUtils.updateStatusBarColor(activity);
        skinAttribute.applySkin();
    }
}
