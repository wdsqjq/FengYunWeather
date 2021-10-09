package me.wsj.plugin_lib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.view.LayoutInflaterCompat;
import androidx.core.view.LayoutInflaterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;

import per.wsj.commonlib.utils.LogUtil;

public class ActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private Observable mObserable;
    private ArrayMap<Activity, LayoutInflaterDelegate> mLayoutInflaterFactories = new ArrayMap<>();

    public ActivityLifecycleCallback(Observable observable) {
        mObserable = observable;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        /**
         *  更新状态栏
         */
//        SkinThemeUtils.updateStatusBarColor(activity);

        try {
            setFactory2(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        hookFactory(activity);
    }

    /**
     * hook Factory方式
     *
     * @param activity
     */
    /*private void hookFactory(Activity activity) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        resetFactory2(layoutInflater);
        LayoutInflater.Factory factory = layoutInflater.getFactory2();

        Class<LayoutInflater> layoutInflaterClass = LayoutInflater.class;
        try {
            Field mFactoryField = layoutInflaterClass.getDeclaredField("mFactory2");
            mFactoryField.setAccessible(true);

            Object proxy = Proxy.newProxyInstance(activity.getClassLoader(),
                    new Class[]{LayoutInflater.Factory2.class}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("onCreateView")) {
                                Object invoke = method.invoke(factory, args);
                                if (invoke != null) {
                                    AttributeSet set = (AttributeSet) args[args.length-1];
                                    // todo getAttribute
                                    LogUtil.e(" invoke：" + invoke);
                                }
                                return invoke;
                            } else {
                                return null;
                            }
                        }
                    });
            mFactoryField.set(layoutInflater, proxy);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("error: " + e);
        }
    }*/

    /**
     * 设置Factory2方式
     * 通过反射修改mFactorySet标志在高版本上已经被限制，但是可以通过反射直接给mFactory2赋值
     * @param activity
     */
    private void setFactory2(Activity activity) throws Exception {
        // 获得Activity的布局加载器
        LayoutInflater layoutInflater = activity.getLayoutInflater();
//        resetFactory2(layoutInflater);

        // 获取到的factory1和factory2是一样的，wtf???
        LayoutInflater.Factory factory1 = layoutInflater.getFactory();
//        LayoutInflater.Factory2 factory2 = layoutInflater.getFactory2();
//        LogUtil.e("factory1: "+ factory1);
//        LogUtil.e("factory2: "+ factory2);

        // 使用factory2 设置布局加载工程
        LayoutInflaterDelegate skinLayoutInflaterFactory = new LayoutInflaterDelegate(activity, factory1);
//        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        Field mFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
        mFactory2Field.setAccessible(true);
        mFactory2Field.set(layoutInflater, skinLayoutInflaterFactory);

        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);

        LogUtil.d("onActivityCreated " + activity.getComponentName().getClassName() + " addObserver");
        mObserable.addObserver(skinLayoutInflaterFactory);
    }

    /**
     * LayoutInflater只能设置一次Factory2,
     * 但是可以重置mFactorySet属性,实现再次设置
     *
     * @param layoutInflater
     */
    /*private void resetFactory2(LayoutInflater layoutInflater) {
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LayoutInflaterDelegate observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
        LogUtil.d("onActivityDestroyed " + activity.getComponentName().getClassName() + " deleteObserver");
    }
}
