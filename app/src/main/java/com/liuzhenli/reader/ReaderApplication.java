package com.liuzhenli.reader;


import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.SharedPreferencesUtil;
import com.liuzhenli.common.utils.AppFrontBackHelper;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.network.DaggerAppComponent;
import com.liuzhenli.reader.module.ApiModule;
import com.liuzhenli.reader.module.AppModule;
import com.liuzhenli.reader.utils.AppUtils;
import com.micoredu.readerlib.content.UpLastChapterModel;
import com.microedu.reader.BuildConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.cconfig.RemoteConfigSettings;
import com.umeng.cconfig.UMRemoteConfig;
import com.umeng.commonsdk.UMConfigure;

import java.util.concurrent.TimeUnit;

/**
 * @author Liuzhenli
 * @since 2019-07-06 16:28
 */
public class ReaderApplication extends BaseApplication {
    public static boolean isDebug = BuildConfig.DEBUG;
    private static ReaderApplication mInstance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
        initComponent();
        AppUtils.init(this);
        AppFrontBackHelper.getInstance().register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                donateHb = System.currentTimeMillis() - SharedPreferencesUtil.getInstance().getLong("DonateHb", 0) <= TimeUnit.DAYS.toMillis(30);
            }

            @Override
            public void onBack() {
                UpLastChapterModel.destroy();
            }
        });

        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
        initUmeng();
    }

    /**
     * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
     * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
     * UMConfigure.init调用中appkey和channel参数请置为null）。
     */
    private void initUmeng() {
        UMRemoteConfig umRemoteConfig = UMRemoteConfig.getInstance();
        UMRemoteConfig.getInstance().setConfigSettings(new RemoteConfigSettings.Builder().setAutoUpdateModeEnabled(true).build());

        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.init(this, "5fb8761f690bda19c786950a", mVersionChannel, UMConfigure.DEVICE_TYPE_PHONE, "");
        //选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
        //建议在宿主App的Application.onCreate函数中调用此函数。
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }


    public static ReaderApplication getInstance() {
        return mInstance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void initComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .build();

    }
}
