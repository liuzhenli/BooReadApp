package com.liuzhenli.common.utils;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.constant.AppConstant;

import java.io.File;
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

    public static File BASE_FOLDER = BaseApplication.getInstance().getExternalFilesDir(null) == null ?
            BaseApplication.getInstance().getFilesDir() :
            BaseApplication.getInstance().getExternalFilesDir(null);

    public static String CAPTURE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/capture/";

    public static String PATH_DATA = FileUtils.createRootPath(BaseApplication.getInstance()) + "/data";
    public static String BASE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/book/";
    public static String FONT_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/font/";
    public static String IMG_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/img/";
    public static String UPDATE_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/update/";
    public static String LOG_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/log/";
    public static String TTS_PATH = FileUtils.createRootPath(BaseApplication.getInstance()) + "/tts/";
    public static String WIFI_BOOK_PATH = FileUtils.createDownloadBookPath(BaseApplication.getInstance()) + "/wifiTransfer/";
    public static final String LOCAL_BOOK_PATH = BASE_FOLDER + "/localBook/";


    public static final String PERCENT_STR = "%";
    public static final String CHARSET_NAME = "UTF-8";

    @Retention(RetentionPolicy.SOURCE)
    public @interface FileAttr {
        String IMAGE = "image";
        String FILE = "file";
        String CHECKED = "checked";
        String SIZE = "size";
        String NAME = "name";
        String DIRECTORY = "directory";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FileSuffix {
        String NONE = "directory";
        String TXT = ".txt";
        String PDF = ".pdf";
        String EPUB = ".epub";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface QQGroup {
        String QQ_1140723995 = "py5-vU4j3y7mobTS3IkZMKKJAFbiKRgl";
        String QQ_272343970 = "UoXpI_hoE2F4he8TsLJ0S3hk2cqLJvVH";
    }

    public static final int MIN_FILE_SIZE = 10;

    public static final String AES_KEY = "oBcnLkp6HcbO0l1QV";
    public static final String ATTACHMENT_SUFFIX_SERVER = ".jpg";
    public static final String ATTACHMENT_SUFFIX = ".t";

    public static final String CONTENT_PREFIX = "content";
}
