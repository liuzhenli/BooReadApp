package com.liuzhenli.reader.bean;

import com.liuzhenli.common.base.BaseBean;

/**
 * Description:
 *
 * @author liuzhenli 2021/1/10
 * Email: 848808263@qq.com
 */
public class AppConfig extends BaseBean {

    public Config data;

    public static class Config {

        public String sourceUrl;
        /**
         * code : 1
         * url :
         * intro : 有新版本
         */
        public LatestVersion versionInfo;
        public DonateInfo pay;
        public AdInfo ad;
        /***bookSource*/
        public BookSource bookSource;
    }

    public static class LatestVersion {
        public int code;
        public String url;
        public String intro;
    }

    public static class DonateInfo {
        public String filterChannel;
    }

    public static class AdInfo {
        public String filterChannel;
    }

    public static class BookSource {
        public String url;
        /**
         * exclude channels eg google,tencent
         */
        public String filterChannel;
    }
}
