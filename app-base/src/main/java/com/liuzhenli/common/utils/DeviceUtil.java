package com.liuzhenli.common.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.liuzhenli.common.encript.MD5Utils;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import nl.siegmann.epublib.domain.Book;

public class DeviceUtil {

    public static String getMobileModel() {
        return Build.MODEL;
    }

    public static String getMobileOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getMobileLanguage() {
        return Locale.getDefault().getCountry();
    }

    //有的机型是取不到或者取到的为空
    public static String getDeviceIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return tm.getDeviceId();
        } else {
            return "";
        }
    }

    public static String getDeviceIMSI(Context context) {
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String imsi = teleManager.getSubscriberId();
            return imsi;
        } else {
            return "";
        }

    }

    //获得品牌
    public static String getDeviceBrand() {
        String carrier = Build.MANUFACTURER;
        return carrier;
    }

    public static String getAndroid_ID(Context ctx) {
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    // 获取设备的唯一ID
    public static String getDeviceUniqueId(Context context) {
        final TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId, simSerial, androidId;

        deviceId = "";
        simSerial = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //没有获取到权限
        } else {
            deviceId = "" + teleManager.getDeviceId();
            simSerial = "" + teleManager.getSimSerialNumber();
        }
        androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | simSerial.hashCode());

        return deviceUuid.toString().toUpperCase();
    }

    // 13 digits which looks like a valid IMEI
    public static String getDeviceCustomIEMI() {
        StringBuilder customIMEI = new StringBuilder("35");
        customIMEI.append(Build.BOARD.length() % 10);
        customIMEI.append(Build.BRAND.length() % 10);
        customIMEI.append(Build.CPU_ABI.length() % 10);
        customIMEI.append(Build.DEVICE.length() % 10);
        customIMEI.append(Build.DISPLAY.length() % 10);
        customIMEI.append(Build.HOST.length() % 10);
        customIMEI.append(Build.ID.length() % 10);
        customIMEI.append(Build.MANUFACTURER.length() % 10);
        customIMEI.append(Build.MODEL.length() % 10);
        customIMEI.append(Build.PRODUCT.length() % 10);
        customIMEI.append(Build.TAGS.length() % 10);
        customIMEI.append(Build.TYPE.length() % 10);
        customIMEI.append(Build.USER.length() % 10);

        return customIMEI.toString();
    }

    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {
        }
        return bool;
    }

    // 是否存在 SD 卡
    public static boolean isExistSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * 获取当前屏幕亮度值
     *
     * @param context
     * @param defaultValue 未取到系统值时的缺省返回值
     * @return
     */
    public static int getScreenBrightness(Context context, int defaultValue) {
        int brightness = defaultValue;

        ContentResolver contentResolver = context.getContentResolver();
        try {
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        return brightness;
    }

    /**
     * 是否锁屏
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            return true;
        }
        return false;
    }

    /**
     * 判断Android应用是否在前台
     * 在小米这类手机中，可能无效,因为小米手机中，appProcess.importance 恒== 100， 原因未知
     */
    public static boolean isAppOnForeground(Context context) {
        String packageName = context.getPackageName();

        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null) return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过是否是当前系统的第一个应用来判断是否是前台应用
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String getTotalMemory() {
        return Math.round((float) Runtime.getRuntime().totalMemory() / 1024 / 1024 * 100) / 100f + "M";
    }

    private static int SLEEPTIME = -1;
    private static boolean isFlag = true; // 是否需要恢复

    public static void restoreScreenSleepTime(Context context) {
        if (isFlag) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, SLEEPTIME);
        }
    }

    public static void setScreenSleepTime(Context context, int value) {
        try {
            // 取出系统当前
            SLEEPTIME = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);

            // 设置当前阅读
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, value);
        } catch (SettingNotFoundException e) {
            isFlag = false;
        }
    }


    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (isFlymeOs4x()) {
            return 2 * statusBarHeight;
        }

        return statusBarHeight;
    }

    /**
     * 判断是不是处理魅族FlymeOS4.x/Android4.4.4
     */
    public static boolean isFlymeOs4x() {
        String sysVersion = Build.VERSION.RELEASE;
        if ("4.4.4".equals(sysVersion)) {
            String sysIncrement = Build.VERSION.INCREMENTAL;
            String displayId = Build.DISPLAY;
            if (!TextUtils.isEmpty(sysIncrement)) {
                return sysIncrement.contains("Flyme_OS_4");
            } else {
                return displayId.contains("Flyme OS 4");
            }
        }
        return false;
    }

    /**
     * 获取设备唯一标识
     */
    public static String getPhoneUid() {
        return MD5Utils.md5(getMacAddress());
    }

    public static String getMacAddress() {
        //该方法在6.0 系统 不再开发，返回 02:00:00:00:00:00
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    /**
     * android 10 及以后的版本
     */
    public static boolean isLaterQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
