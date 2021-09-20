package me.wsj.lib.utils;

import android.content.Context;
import per.wsj.commonlib.utils.SharedPrefUtil;

public class SpUtil {

    private static final String THEME_FLAG = "theme_flag";
    private static final String PLUGIN_PATH = "plugin_path";

    public static int getThemeFlag(Context context) {
        int flag = (int) SharedPrefUtil.getParam(context, THEME_FLAG, 0);
        return flag;
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
}
