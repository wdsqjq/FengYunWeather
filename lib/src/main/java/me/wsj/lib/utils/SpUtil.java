package me.wsj.lib.utils;

import android.content.Context;

import per.wsj.commonlib.utils.SharedPrefUtil;

public class SpUtil {

    private static final String THEME_FLAG = "theme_flag";
    private static final String PLUGIN_PATH = "plugin_path";
    private static final String ACCOUNT_NAME = "account_name";
    private static final String ACCOUNT_AVATAR = "account_avatar";
    private static final String TOKEN = "token";

    public static int getThemeFlag(Context context) {
        return (int) SharedPrefUtil.getParam(context, THEME_FLAG, 0);
    }

    public static void setThemeFlag(Context context, int flag) {
        SharedPrefUtil.setParam(context, THEME_FLAG, flag);
    }

    public static String getPluginPath(Context context) {
        String path = (String) SharedPrefUtil.getParam(context, PLUGIN_PATH, "");
        return path;
    }

    public static void setPluginPath(Context context, String path) {
        SharedPrefUtil.setParam(context, PLUGIN_PATH, path);
    }

    public static String getAccount(Context context) {
        return (String) SharedPrefUtil.getParam(context, ACCOUNT_NAME, "");
    }

    public static void setAccount(Context context, String name) {
        SharedPrefUtil.setParam(context, ACCOUNT_NAME, name);
    }

    public static String getAvatar(Context context) {
        return (String) SharedPrefUtil.getParam(context, ACCOUNT_AVATAR, "");
    }

    public static void setAvatar(Context context, String url) {
        SharedPrefUtil.setParam(context, ACCOUNT_AVATAR, url);
    }

    public static String getToken(Context context) {
        return (String) SharedPrefUtil.getParam(context, TOKEN, "");
    }

    public static void setToken(Context context, String token) {
        SharedPrefUtil.setParam(context, TOKEN, token);
    }

    public static void logout(Context context) {
        setAvatar(context, "");
        setAccount(context, "");
        setToken(context, "");
    }
}
