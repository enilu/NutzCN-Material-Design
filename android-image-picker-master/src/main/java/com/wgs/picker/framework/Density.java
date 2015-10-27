package com.wgs.picker.framework;

import android.content.Context;

/**
 * Created by w.gs on 2015/7/15.
 */
public class Density {

    /**
     * dp 转换为 px
     **/
    public static int of(Context context, int dp_value) {
        return (int) (dp_value * getDensity(context) + 0.5f);
    }

    /**
     * 获取屏幕密度
     **/
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取屏幕宽度
     **/
    public static int getSceenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     **/
    public static int getSceenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     **/
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
