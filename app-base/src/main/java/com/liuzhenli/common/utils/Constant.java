package com.liuzhenli.common.utils;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.constant.AppConstant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Liuzhenli
 * @since 2019-07-06 19:05
 */
public class Constant extends AppConstant {
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";
    public static final String API_BASE_URL = "http://www.baidu.com";
    public static final int TOKEN_EXPRIED = 10104;
    public static final String SP_TOKEN = "token";
    public static final String COMMON_CONFIG = "common_config";
    public static final String REFER = "http://wan.baidu.com";
    public static final String FEEDBACK = "https://support.qq.com/product/186909";

    public static String CAPTURE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/capture/";

    public static String PATH_DATA = FileUtils.createRootPath(BaseApplication.getInstance()) + "/data";
    public static String BASE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/book/";
    public static String FONT_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/font/";
    public static String IMG_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/img/";
    public static String UPDATE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/update/";
    public static String LOG_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/log/";
    public static String TTS_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/tts/";
    public static String WIFI_BOOK_PATH = FileUtils.createWifiBookPath(BaseApplication.getInstance()) + "/wifiTransfer/";
    public static String POST_CACHE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/tiezi/";


    public static final String PERCENTL_STR = "%";
    public static final String CHARSET_NAME = "UTF-8";

    @Retention(RetentionPolicy.SOURCE)
    public @interface FileAttr {
        String IMAGE = "image";
        String FILE = "file";
        String CHECKED = "checked";
        String SIZE = "size";
        String NAME = "name";
        String ZERO = "directory";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Fileuffix {
        String TET = ".txt";
        String PDF = ".pdf";
        String EPUB = ".epub";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface QQGroup {
        String QQ_1140723995 = "py5-vU4j3y7mobTS3IkZMKKJAFbiKRgl";
        String QQ_272343970 = "UoXpI_hoE2F4he8TsLJ0S3hk2cqLJvVH";
    }

    public static final int MIN_FILE_SIZE = 10;
}