package com.liuzhenli.reader.utils;

import android.text.TextUtils;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.utils.ChannelUtil;
import com.liuzhenli.common.utils.IOUtils;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.bean.AppConfig;
import com.liuzhenli.reader.bean.Sayings;
import com.liuzhenli.reader.gson.GsonUtils;
import com.umeng.cconfig.UMRemoteConfig;
import com.umeng.cconfig.UMRemoteConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

/**
 * Description:
 *
 * @author liuzhenli 2021/1/10
 * Email: 848808263@qq.com
 */
public class AppConfigManager {
    public static AppConfigManager instence = new AppConfigManager();

    private AppConfigManager() {
    }

    public static AppConfigManager getInstance() {
        return instence;
    }

    public AppConfig getAppConfig() {
        String str = UMRemoteConfig.getInstance().getConfigValue("appConfig");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return GsonUtils.toBean(str, AppConfig.class);
    }

    public int getNewVersion() {
        if (getAppConfig() != null) {
            if (getAppConfig().data != null && getAppConfig().data.versionInfo != null) {
                return getAppConfig().data.versionInfo.code;
            }
        }
        return 0;
    }

    public String getNewVersionIntro() {
        if (getAppConfig() != null) {
            if (getAppConfig().data != null && getAppConfig().data.versionInfo != null) {
                return getAppConfig().data.versionInfo.intro;
            }
        }
        return "";
    }

    public boolean isShowDonate() {
        if (getAppConfig() != null) {
            if (getAppConfig().data != null && getAppConfig().data.pay != null
                    && !TextUtils.isEmpty(getAppConfig().data.pay.filterChannel)) {
                String[] split = getAppConfig().data.pay.filterChannel.split(",");
                for (String s : split) {
                    if (s.equals(ChannelUtil.getChannelName(BaseApplication.getInstance()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Sayings getSayings() {
        try {
            InputStream open = BaseApplication.getInstance().getAssets().open("sayings.json");
            String json = IOUtils.toString(open);
            open.close();
            List<Sayings> sayings = com.liuzhenli.common.utils.GsonUtils.parseJArray(json, Sayings.class);
            Random random = new Random();
            int i = random.nextInt(sayings.size());
            return sayings.get(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Sayings("发奋识遍天下字，立志读尽人间书", "苏轼");
    }
}
