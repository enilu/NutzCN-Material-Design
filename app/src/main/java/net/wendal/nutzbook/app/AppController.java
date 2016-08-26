package net.wendal.nutzbook.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.market.sdk.*;
import com.xiaomi.mistatistic.sdk.*;

import net.wendal.nutzbook.BuildConfig;

public class AppController extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static String MY_APPID = "2882303761517440917";
    public static String MY_APP_KEY = "5841744096917";
    public static String CHANNEL = "NUTZCN";

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = this;

            try {
                MiStatInterface.initialize(this, MY_APPID, MY_APP_KEY, CHANNEL);
                MiStatInterface.enableExceptionCatcher(true);
                URLStatsRecorder.enableAutoRecord();
            } catch (Exception e){}

            try {
                MiPushClient.registerPush(this, MY_APPID, MY_APP_KEY);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
