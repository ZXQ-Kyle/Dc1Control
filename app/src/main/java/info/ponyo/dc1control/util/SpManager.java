package info.ponyo.dc1control.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * project: Lamp
 * date: 2017-7-21 0021
 * author: CBW
 */
public class SpManager {

    private static volatile SpManager spManager;

    ///////////////////////////////////////////////////////////////////////////
    // static method
    ///////////////////////////////////////////////////////////////////////////
    private SharedPreferences sp;

    private SpManager(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void initInstance(Context context) {
        if (spManager == null) {
            synchronized (SpManager.class) {
                if (spManager == null) {
                    spManager = new SpManager(context);
                }
            }
        }
    }

    /**
     * 获取Preference Editor
     *
     * @return SharedPreferences.Editor
     */
    public static SharedPreferences.Editor getEditor() {
        return getPreference().edit();
    }

    /**
     * 获取Preference
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getPreference() {
        return spManager.sp;
    }

    ///////////////////////////////////////////////////////////////////////////
    // default implement
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 保存字符串
     */
    public static void putString(String key, String val) {
        getEditor().putString(key, val).apply();
    }

    /**
     * 保存字符串
     */
    public static boolean putStringSync(String key, String val) {
        return getEditor().putString(key, val).commit();
    }

    /**
     * 获取字符串
     */
    public static String getString(String key, String defaultVal) {
        String res = defaultVal;
        try {
            res = getPreference().getString(key, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取字符串,缺省值为空
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    /**
     * 保存INT
     */
    public static void putInt(String key, int val) {
        getEditor().putInt(key, val).apply();
    }

    /**
     * 获取INT
     */
    public static int getInt(String key, int defaultVal) {
        int res = defaultVal;
        try {
            res = getPreference().getInt(key, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取INT，缺省值为0
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        boolean res = false;
        try {
            res = getPreference().getBoolean(key, defaultVal);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @return 默认返回false
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

}
