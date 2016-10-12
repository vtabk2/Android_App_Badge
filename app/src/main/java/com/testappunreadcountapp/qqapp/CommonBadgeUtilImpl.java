package com.testappunreadcountapp.qqapp;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import com.testappunreadcountapp.BadgeHelper;
import com.testappunreadcountapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class CommonBadgeUtilImpl {
    public static final String ACTION_APPLICATION_MESSAGE_QUERY = "android.intent.action.APPLICATION_MESSAGE_QUERY";
    public static final String ACTION_APPLICATION_MESSAGE_UPDATE = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
    public static final String ACTION_QQLAUNCHER_BADGE_UPDATE = "com.tencent.qlauncher.action.ACTION_UPDATE_SHORTCUT";
    public static final String EXTRA_UPDATE_APPLICATION_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APPLICATION_MESSAGE_TEXT = "android.intent.extra.update_application_message_text";
    public static final String LENOVO_PACKAGE_NAME = "com.lenovo.launcher";
    public static final String MANUFACTURER_OF_HARDWARE_HUAWEI = "huawei";
    public static final String MANUFACTURER_OF_HARDWARE_LENOVO = "lenovo";
    public static final String MANUFACTURER_OF_HARDWARE_OPPO = "OPPO";
    public static final String MANUFACTURER_OF_HARDWARE_SANXING = "samsung";
    public static final String MANUFACTURER_OF_HARDWARE_SONY = "Sony Ericsson";
    public static final String MANUFACTURER_OF_HARDWARE_VIVO = "vivo";
    public static final String MANUFACTURER_OF_HARDWARE_XIAOMI = "Xiaomi";
    public static final String MANUFACTURER_OF_HARDWARE_ZUK = "ZUK";
    public static final String MANUFACTURER_OF_LENOVO_LAUNCHER_BADGE = "content://com.lenovo.launcher.badge/lenovo_badges";
    public static int NOTIFICATION_ID_NOTIFYID = 110234;
    private static final String OPPO_QQ_BADGE_NUMBER = "com.tencent.mobileqq.badge";
    public static final String TAG = "CommonBadgeUtilImpl";
    public static int haslenovoLanucher = -1;
    private static Boolean haveprovider = null;
    public static String mLauncherClassName = "";
    public static int mLimitCount = 99;
    private static int miui6Flag = 0;
    public static PackageManager packmag;
    private static int sBadgeAbility = 0;
    private static Context sContext;
    public static String[] sQQLuancherPackageNames = new String[]{"com.tencent.qlauncher.lite", "com.tencent.qlauncher", "com.tencent.qqlauncher", "com.tencent.launcher"};

    public static void setLenovoBadge(Context r10, int r11) {
        BadgeHelper.print("Lenovo don't support");

    }

    public static void setSamsungBadge(Context context, int count) {

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

    public static void changeMIBadge(Context context, int count) {
        if (!isMIUI6()) {
            String scount;
            Intent intent = new Intent(ACTION_APPLICATION_MESSAGE_UPDATE);
            intent.putExtra(EXTRA_UPDATE_APPLICATION_COMPONENT_NAME, "com.tencent.mobileqq/.activity.SplashActivity");
            scount = count > 0 ? count > mLimitCount ? "" + mLimitCount : count + "" : "";
            intent.putExtra(EXTRA_UPDATE_APPLICATION_MESSAGE_TEXT, scount);
            context.sendBroadcast(intent);
        } else {
            changeMI6Badge(context, count);
        }
    }

    public static void changeMI6Badge(Context context, int count) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("小米角标")
                .setContentText("miui桌面角标消息");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        Notification notification = builder.build();


        if (count > mLimitCount) {
            count = mLimitCount;
        }
        try {
            Object miuiNotification = Class.forName("android.app.MiuiNotification").newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, Integer.valueOf(count));
            notification.getClass().getField("extraNotification").set(notification, miuiNotification);
            managerCompat.notify(0, notification);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
        } catch (InstantiationException e5) {
            e5.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeZUKBadge(Context context, int count) {
        if (count > mLimitCount) {
            count = mLimitCount;
        }
        try {
            boolean result;
            Bundle extra = new Bundle();
            extra.putStringArrayList("app_shortcut_custom_id", null);
            extra.putInt("app_badge_count", count);
            if (context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extra) != null) {
                result = true;
            } else {
                result = false;
            }
            Log.d(TAG, "changeZUKBadge mcount=" + count + "result=" + result);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void setHuaweiBadge(Context ct, int mcount) {
        int count = mcount;
        try {
            String launcherClassName = getLauncherClassName(ct);
            if (launcherClassName != null) {
                if (count > mLimitCount) {
                    count = mLimitCount;
                }
                Bundle extra = new Bundle();
                extra.putString("package", ct.getPackageName());
                extra.putString("class", launcherClassName);
                extra.putInt("badgenumber", count);
                ct.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
            }
        } catch (Throwable th) {
        }
    }

    public static void changeOPPOBadge(Context context, int count) {
        if (count > mLimitCount) {
            count = mLimitCount;
        }
        try {
            Bundle extras = new Bundle();
            extras.putInt("app_badge_count", count);
            context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", String.valueOf(count), extras);
        } catch (Throwable th) {
        }
    }

    public static String getLauncherClassName(Context context) {
        if (!TextUtils.isEmpty(mLauncherClassName)) {
            return mLauncherClassName;
        }
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        try {
            for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
                if (resolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                    String className = resolveInfo.activityInfo.name;
                    mLauncherClassName = className;
                    return className;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setSonyBadge(Context context, int count) {
        Intent intent = new Intent();
        String launcherclassname = getLauncherClassName(context);
        if (launcherclassname != null) {
            String unread;
            if (count < 1) {
                unread = "";
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", false);
            } else {
                unread = count > mLimitCount ? "" + mLimitCount : count + "";
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
            }
            intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherclassname);
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", unread);
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            context.sendBroadcast(intent);
        }
    }

    public static void changeVivoBadge(Context context, int count) {
        if (count > mLimitCount) {
            count = mLimitCount;
        }
        String launcherclassname = getLauncherClassName(context);
        if (launcherclassname != null) {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra(Constants.KEY_PKG_NAME, context.getPackageName());
            intent.putExtra(Constants.KEY_CLASS_NAME, launcherclassname);
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        }
    }

    public static void setBadge(Context context, int count, boolean forceSet) {
        Log.d(TAG, "setBadge count=" + count + "|forceSet=" + forceSet);
//        BadgeController.setBadge(count);
//        if (isQQLanucher()) {
//            setQQLauncherBadges(context, count);//qq
//        }
        if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_ZUK)) {
            changeZUKBadge(context, count);//zuk
        }
        if (islenovoLanucher(LENOVO_PACKAGE_NAME)) {
            setLenovoBadge(context, count);//联想
        }
        if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_XIAOMI)) {
            changeMIBadge(context, count);//小米 no
        } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_SANXING)) {
            setSamsungBadge(context, count);//三星 ok
        } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_HUAWEI)) {
            setHuaweiBadge(context, count);//华为 ok
        } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_OPPO)) {
            changeOPPOBadge(context, count);//oppo no
        } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_VIVO)) {
            changeVivoBadge(context, count);//vivo ok
        } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_SONY)) {
            setSonyBadge(context, count);//sony ok
        }
    }

    public static void removeBadge(Context context) {
        setBadge(context,0);
    }

    public static void setLimitCount(int limit) {
        mLimitCount = limit;
    }

    public static void setBadge(Context ctx, int count) {
        setBadge(ctx, count, false);
    }

    public static void setMIUI6Badge(Context ctx, int count, Notification notification) {
        if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_XIAOMI) && isMIUI6()) {
            changeMI6Badge(ctx, count);
        }
    }

    public static boolean isSupportBadge(Context context) {
        int i = 1;
        if (sContext == null) {
            sContext = context;
        }
        if (sBadgeAbility == 0) {
            boolean isSupport;
            if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_ZUK)) {
                isSupport = true;
            } else if (islenovoLanucher(LENOVO_PACKAGE_NAME) || isQQLanucher()) {
                isSupport = true;
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_XIAOMI)) {
                isSupport = true;
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_SANXING)) {
                isSupport = true;
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_HUAWEI)) {
                isSupport = true;
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_OPPO)) {
                isSupport = true;
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_VIVO)) {
                isSupport = true;
            } else {
                isSupport = false;
            }
            BadgeController.init(sContext);
            if (BadgeController.isSupport(sContext)) {
                isSupport = true;
            }
            if (!isSupport) {
                i = 2;
            }
            sBadgeAbility = i;
            return isSupport;
        } else if (sBadgeAbility == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isQQLanucher() {
        if (packmag == null) {
            packmag = sContext.getPackageManager();
        }
        int i = 0;
        while (i < sQQLuancherPackageNames.length) {
            try {
                if (packmag.getPackageInfo(sQQLuancherPackageNames[i], 0) != null) {
                    return true;
                }
                i++;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static void setQQLauncherBadges(Context ctx, int count) {
        if (count > mLimitCount) {
            count = mLimitCount;
        } else if (count < 1) {
            count = 0;
        }
        Intent i = new Intent(ACTION_QQLAUNCHER_BADGE_UPDATE);
        i.putExtra("webappId", "20634");
        i.putExtra("info_tips", String.valueOf(count));
        ctx.sendBroadcast(i);
    }

    public static boolean isMIUI6() {
        int MICode;
        if (miui6Flag == 0) {
            miui6Flag = -1;
            String line = "";
            BufferedReader input = null;
            try {
                BufferedReader input2 = new BufferedReader(new InputStreamReader(new ProcessBuilder(new String[]{"getprop", "ro.miui.ui.version.code"}).start().getInputStream()), 1024);
                try {
                    line = input2.readLine();
                    input2.close();
                    if (input2 != null) {
                        try {
                            input2.close();
                        } catch (IOException e) {
                        }
                    }
                    if (!TextUtils.isEmpty(line)) {
                        MICode = 0;
                        try {
                            MICode = Integer.parseInt(line);
                        } catch (NumberFormatException e2) {
                            e2.printStackTrace();
                        }
                        if (MICode > 3) {
                            miui6Flag = 1;
                        }
                    }
                } catch (IOException e3) {
                    input = input2;
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e4) {
                        }
                    }
                    if (!TextUtils.isEmpty(line)) {
                        MICode = 0;
                        try {
                            MICode = Integer.parseInt(line);
                        } catch (NumberFormatException e22) {
                            e22.printStackTrace();
                        }
                        if (MICode > 3) {
                            miui6Flag = 1;
                        }
                    }
                    if (miui6Flag == 1) {
                        return false;
                    }
                    return true;
                } catch (Exception e5) {
                    input = input2;
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e6) {
                        }
                    }
                    if (!TextUtils.isEmpty(line)) {
                        MICode = 0;
                        try {
                            MICode = Integer.parseInt(line);
                        } catch (NumberFormatException e222) {
                            e222.printStackTrace();
                        }
                        if (MICode > 3) {
                            miui6Flag = 1;
                        }
                    }
                    if (miui6Flag == 1) {
                        return true;
                    }
                    return false;
                } catch (Throwable th2) {
                    input = input2;
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e7) {
                        }
                    }
                    if (!TextUtils.isEmpty(line)) {
                        MICode = 0;
                        try {
                            MICode = Integer.parseInt(line);
                        } catch (NumberFormatException e2222) {
                            e2222.printStackTrace();
                        }
                        if (MICode > 3) {
                            miui6Flag = 1;
                        }
                    }
                }
            } catch (IOException e8) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(line)) {
                    MICode = 0;
                    MICode = Integer.parseInt(line);
                    if (MICode > 3) {
                        miui6Flag = 1;
                    }
                }
                if (miui6Flag == 1) {
                    return false;
                }
                return true;
            } catch (Exception e9) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(line)) {
                    MICode = 0;
                    MICode = Integer.parseInt(line);
                    if (MICode > 3) {
                        miui6Flag = 1;
                    }
                }
                if (miui6Flag == 1) {
                    return true;
                }
                return false;
            } catch (Throwable th3) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(line)) {
                    MICode = 0;
                    MICode = Integer.parseInt(line);
                    if (MICode > 3) {
                        miui6Flag = 1;
                    }
                }
            }
        }
        if (miui6Flag == 1) {
            return true;
        }
        return false;
    }

    public static boolean islenovoLanucher(String packageName) {
        if (VERSION.SDK_INT < 15) {
            return false;
        }
        if (haslenovoLanucher == -1) {
            try {
                if (packmag == null && sContext != null) {
                    packmag = sContext.getPackageManager();
                }
                if (Float.valueOf(Float.parseFloat(packmag.getPackageInfo(packageName, 0).versionName.substring(0, 3))).floatValue() >= 6.7f) {
                    haslenovoLanucher = 1;
                    return true;
                }
                haslenovoLanucher = 0;
                return false;
            } catch (NameNotFoundException e) {
                haslenovoLanucher = 0;
                return false;
            } catch (Exception e2) {
                haslenovoLanucher = 0;
                return false;
            }
        } else if (haslenovoLanucher == 1) {
            return true;
        } else {
            return false;
        }
    }
}
