package com.liuzhenli.common;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;

import com.liuzhenli.common.constant.AppConstant;
import com.liuzhenli.common.utils.AppFrontBackHelper;
import com.liuzhenli.common.utils.ChannelUtil;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-11-10 15:58
 */
public class BaseApplication extends Application {
    public final static String channelIdDownload = "channel_download";
    public final static String channelIdReadAloud = "channel_read_aloud";
    public final static String channelIdWeb = "channel_web";
    private static BaseApplication sInstance;
    private static String downloadPath;
    protected boolean donateHb;
    public int mVersionCode;
    public String mVersionName;
    public String mVersionChannel;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        initMMKV();
        SharedPreferencesUtil.init(this);
        QMUISwipeBackActivityManager.init(this);
        sInstance = this;
        downloadPath = SharedPreferencesUtil.getInstance().getString(getString(R.string.pk_download_path), "");
        if (TextUtils.isEmpty(downloadPath) | Objects.equals(downloadPath, FileHelp.getCachePath())) {
            setDownloadPath(null);
        }
        initAppVersion();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelId();
        }
    }

    private void initAppVersion() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionCode = pi.versionCode;
            mVersionName = pi.versionName;
            mVersionChannel = ChannelUtil.getChannelName(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置下载地址
     */
    public void setDownloadPath(String path) {
        if (TextUtils.isEmpty(path)) {
            downloadPath = FileHelp.getFilesPath();
        } else {
            downloadPath = path;
        }
        AppConstant.BOOK_CACHE_PATH = downloadPath + File.separator + "book_cache" + File.separator;
        SharedPreferencesUtil.getInstance().putString(getString(R.string.pk_download_path), path);
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * 创建通知ID
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelId() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //用唯一的ID创建渠道对象
        NotificationChannel downloadChannel = new NotificationChannel(channelIdDownload,
                getString(R.string.download_offline),
                NotificationManager.IMPORTANCE_LOW);
        //初始化channel
        downloadChannel.enableLights(false);
        downloadChannel.enableVibration(false);
        downloadChannel.setSound(null, null);

        //用唯一的ID创建渠道对象
        NotificationChannel readAloudChannel = new NotificationChannel(channelIdReadAloud,
                getString(R.string.read_aloud),
                NotificationManager.IMPORTANCE_LOW);
        //初始化channel
        readAloudChannel.enableLights(false);
        readAloudChannel.enableVibration(false);
        readAloudChannel.setSound(null, null);

        //用唯一的ID创建渠道对象
        NotificationChannel webChannel = new NotificationChannel(channelIdWeb,
                getString(R.string.web_service),
                NotificationManager.IMPORTANCE_LOW);
        //初始化channel
        webChannel.enableLights(false);
        webChannel.enableVibration(false);
        webChannel.setSound(null, null);

        //向notification manager 提交channel
        if (notificationManager != null) {
            notificationManager.createNotificationChannels(Arrays.asList(downloadChannel, readAloudChannel, webChannel));
        }
    }

    public boolean getDonateHb() {
        return donateHb || com.liuzhenli.common.BuildConfig.DEBUG;
    }

    public void upDonateHb() {
        SharedPreferencesUtil.getInstance().putLong("DonateHb", System.currentTimeMillis());
        donateHb = true;
    }

    public void upThemeStore() {
    }

    public void initNightTheme() {
    }

    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
    }

}
