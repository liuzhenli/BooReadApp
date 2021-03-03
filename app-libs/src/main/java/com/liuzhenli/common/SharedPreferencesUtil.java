package com.liuzhenli.common;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;


import com.tencent.mmkv.MMKV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author liuzhenli
 */
public class SharedPreferencesUtil {

    private volatile static SharedPreferencesUtil prefsUtil;
    public Application context;
    public MMKV prefs;
    public SharedPreferences.Editor editor;

    public synchronized static SharedPreferencesUtil getInstance() {
        return prefsUtil;
    }

    public static void init(Application context) {
        prefsUtil = new SharedPreferencesUtil();
        prefsUtil.context = context;
        prefsUtil.prefs = MMKV.mmkvWithID("config");
        // 迁移旧数据
        {
            SharedPreferences old_man = prefsUtil.context.getSharedPreferences("config", Context.MODE_PRIVATE);
            prefsUtil.prefs.importFromSharedPreferences(old_man);
            old_man.edit().clear().apply();
        }
        prefsUtil.editor = prefsUtil.prefs.edit();
    }

    private SharedPreferencesUtil() {
    }


    public boolean getBoolean(String key, boolean defaultVal) {
        return this.prefs.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }


    public String getString(String key, String defaultVal) {
        return this.prefs.getString(key, defaultVal);
    }

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public int getInt(String key, int defaultVal) {
        return this.prefs.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return this.prefs.getInt(key, 0);
    }


    public float getFloat(String key, float defaultVal) {
        return this.prefs.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return this.prefs.getFloat(key, 0f);
    }

    public long getLong(String key, long defaultVal) {
        return this.prefs.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return this.prefs.getLong(key, 0l);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return this.prefs.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key) {
        return this.prefs.getStringSet(key, null);
    }

    public Map<String, ?> getAll() {
        return this.prefs.getAll();
    }


    public SharedPreferencesUtil putString(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    public SharedPreferencesUtil putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public SharedPreferencesUtil putFloat(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    public SharedPreferencesUtil putLong(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    public SharedPreferencesUtil putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferencesUtil putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);

        return this;
    }

    public void putObject(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor.putString(key, objectVal);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        if (prefs.contains(key)) {
            String objectVal = prefs.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public SharedPreferencesUtil remove(String key) {
        editor.remove(key);
        return this;
    }

    public String[] getArray(String key) {
        String regularEx = "#";
        String[] str;
        String values;
        values = prefs.getString(key, "");
        str = values.split(regularEx);
        return str;
    }


    public void putArray(String key, String[] values) {
        String regularEx = "#";
        String str = "";
        if (values != null && values.length > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            editor.putString(key, str);
        }
    }

    public int[] getIntArray(String key) {
        String regularEx = "#";
        String values;
        values = prefs.getString(key, "-1");
        String[] strings = values.split(regularEx);
        int[] str = new int[strings.length];
        for (int i = 0; i < str.length && str.length > 0; i++) {
            try {
                str[i] = Integer.parseInt(strings[i]);
            } catch (Exception e) {
                e.printStackTrace();
                return str;
            }
        }
        return str;
    }

    public void putArray(String key, int[] values) {
        String regularEx = "#";
        String str = "";
        if (values != null && values.length > 0) {
            for (int value : values) {
                str += value;
                str += regularEx;
            }
            editor.putString(key, str);
        }
    }
}
