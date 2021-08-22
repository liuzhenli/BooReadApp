package com.liuzhenli.common.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.liuzhenli.common.SharedPreferencesUtil;
import com.liuzhenli.common.constant.AppConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * app 配置管理类
 *
 * @author Liuzhenli
 * @since 2019-07-06 16:28
 */
public class AppSharedPreferenceHelper {

    /***书源排序方式*/
    public interface SortType {
        /***手动排序  SerialNumber*/
        int SORT_TYPE_HAND = 0;
        /***智能排序 weight*/
        int SORT_TYPE_AUTO = 1;
        /***音序排序 bookSourceName*/
        int SORT_TYPE_PINYIN = 2;
    }

    /***自动备份网络设置*/
    public static final String AUTO_BACKUP_NET_TYPE = "auto_backup_wifi_type";

    public interface BackupNetType {
        int ALL_ALLOWED = 0;
        int WIFI_ONLY = 1;
    }

    public static void setBookSourceSortType(int sortType) {
        SharedPreferencesUtil.getInstance().putInt("SourceSort", sortType);
    }

    public static int getBookSourceSortType() {
        return SharedPreferencesUtil.getInstance().getInt("SourceSort", SortType.SORT_TYPE_PINYIN);
    }

    /***手机文件,最小值*/
    public static int getMinFileSize() {
        return SharedPreferencesUtil.getInstance().getInt("mine_file_size", Constant.MIN_FILE_SIZE);
    }

    /**
     * 设置文件最小值
     *
     * @param size 最小值
     */
    public static void setMineFileSize(int size) {
        SharedPreferencesUtil.getInstance().putInt("mine_file_size", size);
    }

    /**
     * 检查书架书的更新
     */
    public static boolean isRefreshBookShelf() {
        return SharedPreferencesUtil.getInstance().getBoolean("refresh_book_shelf", true);
    }

    /**
     * 设置书架是否开机更新
     *
     * @param refresh 是否更新
     */
    public static void setRefreshBookShelf(boolean refresh) {
        SharedPreferencesUtil.getInstance().putBoolean("refresh_book_shelf", refresh);
    }


    public static void setProtectEyeReadMode(boolean on) {
        SharedPreferencesUtil.getInstance().putBoolean("protect_eye_read_mode", on);
    }

    public static boolean tetProtectEyeReadMode() {
        return SharedPreferencesUtil.getInstance().getBoolean("protect_eye_read_mode", false);
    }


    /***备份及恢复****/
    public static String getWebDavAddress() {
        return SharedPreferencesUtil.getInstance().getString("webDavAddress", AppConstant.DEFAULT_WEB_DAV_URL);
    }

    public static void saveWebDavAddress(String webDavAddress) {
        putString("webDavAddress", webDavAddress);
    }

    public static String getWebDavAccountName() {
        return SharedPreferencesUtil.getInstance().getString("webDavAccountName");
    }

    public static void saveWebDavAccountName(String webDavAccount) {
        putString("webDavAccountName", webDavAccount);
    }

    public static String getWebDavAddPwd() {
        return SharedPreferencesUtil.getInstance().getString("webDavAccountPassword");
    }

    public static void saveWebDavAddPwd(String password) {
        putString("webDavAccountPassword", password);
    }

    /***备份地址*/
    public static void setBackupPath(String backUpPath) {
        putString("backupPath", backUpPath);
    }

    /*** 备份地址*/
    public static String getBackupPath(String defaultPath) {
        return getString("backupPath", defaultPath);
    }

    public static void putString(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            SharedPreferencesUtil.getInstance().remove(key);
        } else {
            SharedPreferencesUtil.getInstance().putString(key, value);
        }
    }

    public static String getString(String key, String defaultValue) {
        return SharedPreferencesUtil.getInstance().getString(key, defaultValue);
    }

    /***save last page info**/
    public static void saveLastPage(Map<String, Object> data) {
        if (data == null) {
            clearValue("lastPage");
        } else {
            saveMap("lastPage", data);
        }
    }

    /**
     * @return saved page info
     */
    public static Map<String, Object> getLastPageInfo() {
        return (Map<String, Object>) getMap("lastPage");
    }

    public static void clearValue(String key) {
        SharedPreferencesUtil.getInstance().remove(key);
    }

    public static void saveMap(String key, Map<?, ?> map) {
        try {
            ByteArrayOutputStream toByte = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(toByte);
            oos.writeObject(map);
            // 对byte[]进行Base64编码
            String lastGlobalMsgIds = new String(Base64.encode(toByte.toByteArray(), Base64.DEFAULT));
            SharedPreferencesUtil.getInstance().putString(key, lastGlobalMsgIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<?, ?> getMap(String key) {
        try {
            byte[] base64Bytes = Base64.decode(getString(key, ""), Base64.DEFAULT);
            if (base64Bytes.length > 0) {
                ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (Map<?, ?>) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static String getImportLocalBookPath() {
        return SharedPreferencesUtil.getInstance().getString("import_local_book_path");
    }

    public static void setImportLocalBookPath(String path) {
        SharedPreferencesUtil.getInstance().putString("import_local_book_path", path);
    }
}
