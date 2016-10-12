package com.testappunreadcountapp.qqapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

public final class BadgeController {
    private static final String[] LAUNCHER_WHITE_LIST = new String[]{"com.tct.launcher", "com.bbk.studyos.launcher"};
    private static final String TAG = "BadgeUtils";
    private static Context sApplication;
    private static String sCurBadgeProviderAuthorities;
    private static String sCurLauncherPackageName;
    private static boolean sDebug = false;
    private static volatile Integer sNextCount;
    private static volatile boolean sResumed = true;

    public static void resumeOrPause(boolean resume) {
        sResumed = resume;
        Integer nextCount = sNextCount;
        if (sResumed && nextCount != null) {
            setBadge(nextCount.intValue());
        }
    }

    public static void init(Context context) {
        sApplication = context.getApplicationContext();
        String packageName = getCurLauncherPackageName();
        boolean in = false;
        for (String wpn : LAUNCHER_WHITE_LIST) {
            if (wpn.equalsIgnoreCase(packageName)) {
                in = true;
                break;
            }
        }
        if (sDebug) {
            sCurLauncherPackageName = packageName;
        } else if (in) {
            sCurLauncherPackageName = packageName;
        }
        if (sCurLauncherPackageName != null) {
            try {
                ApplicationInfo ai = sApplication.getPackageManager().getApplicationInfo(sCurLauncherPackageName, 128);
                if (ai != null) {
                    sCurBadgeProviderAuthorities = ai.metaData.getString("badge_provider");
                }
            } catch (Exception e) {
            }
        }
    }

    public static boolean isSupport(Context context) {
        if (sCurLauncherPackageName == null) {
            init(context);
        }
        return sCurBadgeProviderAuthorities != null;
    }

    public static boolean setBadge(int count) {
        if (sResumed) {
            sNextCount = null;
        } else {
            sNextCount = Integer.valueOf(count);
        }
        if (sCurBadgeProviderAuthorities == null) {
            return false;
        }
        Uri uri = Uri.parse("content://" + sCurBadgeProviderAuthorities + "/badge");
        Bundle b = new Bundle();
        b.putInt("count", count);
        return sApplication.getContentResolver().call(uri, "setBadge", "", b).getBoolean("result");
    }

    private static String getCurLauncherPackageName() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo res = sApplication.getPackageManager().resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null || res.activityInfo.packageName.equals("android")) {
            return null;
        }
        return res.activityInfo.packageName;
    }
}
