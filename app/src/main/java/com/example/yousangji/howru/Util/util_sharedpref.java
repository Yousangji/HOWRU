package com.example.yousangji.howru.Util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class util_sharedpref {


    private static util_sharedpref util_shpref;
    private SharedPreferences mPreference;
    public static void createInstance(Context ctx) {
        util_shpref = new util_sharedpref(ctx);
    }
    public static util_sharedpref getInstance() {
        return util_shpref;
    }
    private util_sharedpref(Context ctx) {
        mPreference = ctx.getApplicationContext().getSharedPreferences(ctx.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }
    public boolean putString(String key, String value) {
        return mPreference.edit().putString(key, value).commit();
    }
    public boolean putInt(String key, int value) {
        return mPreference.edit().putInt(key, value).commit();
    }
    public boolean putBoolean(String key, boolean value) {
        return mPreference.edit().putBoolean(key, value).commit();
    }
    public boolean putValues(ContentValues values) {
        SharedPreferences.Editor editor = mPreference.edit();
        for (Map.Entry<String, Object> value : values.valueSet()) {
            editor.putString(value.getKey(), value.getValue().toString());
        }
        return editor.commit();
    }
    public String getString(String key) {
        return getString(key, "");
    }
    public String getString(String key, String defValue) {
        return mPreference.getString(key, defValue);
    }
    public int getInt(String key) {
        return getInt(key, -1);
    }
    public int getInt(String key, int defValue) {
        return mPreference.getInt(key, defValue);
    }
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    public boolean getBoolean(String key, boolean defValue) {
        return mPreference.getBoolean(key, defValue);
    }
}
