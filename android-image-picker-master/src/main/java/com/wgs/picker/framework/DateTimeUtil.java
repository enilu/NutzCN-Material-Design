package com.wgs.picker.framework;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by w.gs on 2015/7/17.
 */
public class DateTimeUtil {

    public static String formatDate(long millionseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millionseconds);
        return sdf.format(calendar.getTime());
    }

    public static String formatDateTime(long millionseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millionseconds);
        return sdf.format(calendar.getTime());
    }
}
