package me.wsj.lib.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class DensityUtil {
    private static float mAppDensity;
    private static float mAppScaledDensity;
    private static DisplayMetrics mDisplayMetrics;
    /**
     * 用来参照的的width
     */
    private static float mWidth;

    /**
     * 在Application中调用
     *
     * @param application
     * @param width       设计稿的宽度（dp）
     */
    public static void setDensity(@NonNull final Application application, float width) {
        mDisplayMetrics = application.getResources().getDisplayMetrics();
        mWidth = width;
        registerActivityLifecycleCallbacks(application);

        if (mAppDensity == 0) {
            // 初始化的时候赋值
            mAppDensity = mDisplayMetrics.density;
            mAppScaledDensity = mDisplayMetrics.scaledDensity;

            // 添加字体变化的监听
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    // 字体改变后,将appScaledDensity重新赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mAppScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
    }


    private static void setDefault(Activity activity) {
        setAppOrientation(activity);
    }

    private static void setAppOrientation(@Nullable Activity activity) {
        float targetDensity = 0;
        try {
            int orientation = activity.getResources().getConfiguration().orientation;
            // 横屏则使用高度计算比例
            if (orientation == ORIENTATION_LANDSCAPE) {
                targetDensity = mDisplayMetrics.heightPixels / mWidth;
            } else {
                targetDensity = mDisplayMetrics.widthPixels / mWidth;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        float targetScaledDensity = targetDensity * (mAppScaledDensity / mAppDensity);
        int targetDensityDpi = (int) (160 * targetDensity);

        // 最后在这里将修改过后的值赋给系统参数,只修改Activity的density值
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    private static void registerActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                setDefault(activity);
            }

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

            }
        });
    }
}
