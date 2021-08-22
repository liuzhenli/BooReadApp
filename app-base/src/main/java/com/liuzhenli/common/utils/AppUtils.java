/**
 * created by yuyh, 16/04/09
 * Copyright (c) 2016, smuyyh@gmail.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */


package com.liuzhenli.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;

import com.liuzhenli.common.BaseApplication;
import com.meituan.android.walle.WalleChannelReader;

public class AppUtils {

    private static Context mContext;
    private static Thread mUiThread;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void init(Context context) {
        mContext = context;
        mUiThread = Thread.currentThread();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static AssetManager getAssets() {
        return mContext.getAssets();
    }

    public static Resources getResource() {
        return mContext.getResources();
    }

    public static boolean isUIThread() {
        return Thread.currentThread() == mUiThread;
    }

    public static void runOnUI(Runnable r) {
        sHandler.post(r);
    }

    public static void runOnUIDelayed(Runnable r, long delayMills) {
        sHandler.postDelayed(r, delayMills);
    }

    public static void removeRunnable(Runnable r) {
        if (r == null) {
            sHandler.removeCallbacksAndMessages(null);
        } else {
            sHandler.removeCallbacks(r);
        }
    }

    /**
     * @param url 源url
     */
    public static String resetGameUrl(String url) {
        return url;
    }

    public static int getAppVersionCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

    public static String getAppVersionName(Context ctx) {
        String appVersionName = "null";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            // 版本名
            appVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }

    public static String getChannelValue(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String meituan_channel = "";
        try {
            meituan_channel = WalleChannelReader.getChannel(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String UMENG_CHANNEL = appInfo.metaData.getString("UMENG_CHANNEL");
        if (TextUtils.isEmpty(meituan_channel)) {
            return UMENG_CHANNEL;
        }
        return meituan_channel;
    }

    public static int getChannelCodeValue(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int meituan_channel_code = 0;
        int UMENG_CHANNEL_CODE = 0;
        UMENG_CHANNEL_CODE = appInfo.metaData.getInt("CHNL");
        try {
            if (WalleChannelReader.get(context, "CHNL") != null) {
                meituan_channel_code = Integer.parseInt(WalleChannelReader.get(context, "CHNL"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (meituan_channel_code == 0) {
            return UMENG_CHANNEL_CODE;
        }
        return meituan_channel_code;
    }

    public static String getFormatDeviceUnique() {
        String uuid = DeviceUtil.getDeviceUniqueId(BaseApplication.getInstance());
        byte[] data = ("unique_id:" + uuid).getBytes();
        return new String(Base64.encode(data, Base64.DEFAULT));
    }

    public static String getFormatVersionCode() {
        byte[] data = ("app_version:android-" + getAppVersionCode(BaseApplication.getInstance())).getBytes();
        return new String(Base64.encode(data, Base64.DEFAULT));
    }
}
