package me.wsj.fengyun.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.SparseArray;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import me.wsj.fengyun.R;
import me.wsj.fengyun.bean.Now;
import me.wsj.fengyun.ui.activity.SplashActivity;
import me.wsj.lib.utils.IconUtils;


public class NotificationUtil {

    private static final String CHANNEL_ID = "fengyun_notify_id";
    private static final String CHANNEL_NAME = "天气实况";

    private static SparseArray<NotificationCompat.Builder> notificationMap = new SparseArray<>();

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 创建或更新
     *
     * @param context
     * @param notifyId
     * @return
     */
    public static Notification createNotification(Context context, int notifyId) {
        NotificationCompat.Builder builder = notificationMap.get(notifyId);
        if (builder == null) {
            builder = initBaseBuilder(context, "", "", R.mipmap.ic_launcher_round);
            // 点击事件
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(contentIntent);
            notificationMap.put(notifyId, builder);
        }
        // 布局
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_weather_notify);
        builder.setCustomContentView(views);

        Notification notification = builder.build();

        return notification;
    }

    public static void updateNotification(Context context, int notifyId, String cityName, Now now) {
        NotificationCompat.Builder builder = notificationMap.get(notifyId);
        if (builder == null) {
            return;
        }

        // 布局
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_weather_notify);
        builder.setCustomContentView(views);

        views.setTextViewText(R.id.tvLocation, cityName);
        if (now != null) {
            views.setTextViewText(R.id.tvWeather, now.getText());
            views.setTextViewText(R.id.tvTemp, now.getTemp() + "°C");
            if (IconUtils.isDay()) {
                views.setImageViewResource(R.id.ivWeather, IconUtils.getDayIconDark(context, now.getIcon()));
            } else {
                views.setImageViewResource(R.id.ivWeather, IconUtils.getNightIconDark(context, now.getIcon()));
            }
        }

        getNotificationManager(context).notify(notifyId, builder.build());
    }


    /**
     * 初始化Builder
     *
     * @param context
     * @param title
     * @param content
     * @param icon
     * @return
     */
    private static NotificationCompat.Builder initBaseBuilder(Context context, String title, String content, int icon) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.enableLights(true); // 闪光
            channel.setLightColor(Color.RED);   // 闪光时的灯光颜色
//            channel.canShowBadge();         // 桌面launcher显示角标
            channel.enableVibration(false);  // 是否震动
            channel.setShowBadge(false);
            channel.shouldShowLights();//是否会闪光

            getNotificationManager(context).createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                // 是否常驻,true为常驻
                .setOngoing(true)
                .setNotificationSilent()
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        return builder;
    }


    /**
     * 创建进度通知栏
     *
     * @param context
     * @param title
     * @param content
     * @param icon
     */
    public static void createProgressNotification(Context context, String title, String content, int icon, int notifyId) {
        NotificationCompat.Builder builder = initBaseBuilder(context, title, content, icon);
        builder.setOngoing(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Intent intent = new Intent();
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(contentIntent);
        }

        getNotificationManager(context).notify(notifyId, builder.build());

        notificationMap.put(notifyId, builder);
    }

    /**
     * 取消进度通知栏
     *
     * @param notifyId
     */
    public static void cancelNotification(Context context, int notifyId) {
        getNotificationManager(context).cancel(notifyId);
        notificationMap.remove(notifyId);
    }

    /**
     * 更新通知栏进度
     *
     * @param notifyId
     * @param progress
     */
    public static void updateNotification(Context context, int notifyId, float progress) {
        NotificationCompat.Builder builder = notificationMap.get(notifyId);
        builder.setProgress(100, (int) progress, false);
        builder.setContentText(progress + "%");
        getNotificationManager(context).notify(notifyId, builder.build());
    }
}
