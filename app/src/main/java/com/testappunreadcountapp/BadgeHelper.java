package com.testappunreadcountapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by dingbaosheng on 16/10/9.
 */
public class BadgeHelper {


    //必须使用，Activity启动页
    private final static String lancherActivityClassName = MainActivity.class.getName();

    public static void setBadgeCount(Context context, int count) {

        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, count);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
            sendToSony(context, count);//can work
        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            sendToSamsumg(context, count);//can work
        } else {
            Toast.makeText(context, "Not Support", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 向小米手机发送未读消息数广播
     *
     * @param count
     */
    private static void sendToXiaoMi(Context context, int number) {

        sendToXiaomi6(context,number);
        /*NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有" + number + "未读消息");
            builder.setTicker("您有" + number + "未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            //以上代码为notification的初始化信息，在实际应用中，可以单独使用

            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);

            field.set(miuiNotification, number);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);

            field.set(notification, miuiNotification);
            Toast.makeText(context, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");

            localIntent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName() + "/" + getLauncherClassName(context));
            localIntent.putExtra("android.intent.extra.update_application_message_text", number);
            context.sendBroadcast(localIntent);
        } finally {
            if (notification != null && isMiUIV6) {
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }*/

    }


    private static void sendToXiaomi6(Context context, int count) {
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("小米角标")
                .setContentText("miui桌面角标消息");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        Notification notification = builder.build();
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, count);
        } catch (Exception e) {
            e.printStackTrace();
        }

        managerCompat.notify(0, notification);*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("小米角标")
                .setContentText("miui桌面角标消息");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        Notification notification = builder.build();

        try {

            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, count);

        } catch (Exception e) {

            e.printStackTrace();

        }

        managerCompat.notify(0,notification);
    }

    /**
     * 向索尼手机发送未读消息数广播<br/>
     * 据说：需添加权限：<uses-permission android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" /> [未验证]
     *
     * @param count
     */
    private static void sendToSony(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }


    /**
     * 向三星手机发送未读消息数广播
     *
     * @param count
     */
    private static void sendToSamsumg(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }


    /**
     * 重置、清除Badge未读显示数<br/>
     *
     * @param context
     */
    public static void resetBadgeCount(Context context) {
        setBadgeCount(context, 0);
    }


    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }


    public static void sendNativeNotification(Context context, int i) {
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("new message " + i)
                .setContentText("twain@android.com " + i);
        mBuilder.setTicker("New message " + i);//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(12);
        mBuilder.setLargeIcon(btm);
        mBuilder.setAutoCancel(true);//自己维护通知的消失

        //构建一个Intent
        Intent resultIntent = new Intent(context,
                BActivity.class);
        //封装一个Intent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 设置通知主题的意图
        mBuilder.setContentIntent(resultPendingIntent);
        //获取通知管理器对象
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }


    public static void print(String msg) {
        Log.d("dbs", msg);
    }
}
