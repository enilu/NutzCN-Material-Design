package net.wendal.nutzbook.app;

import android.app.Application;
import android.content.Context;

import com.umeng.update.UpdateConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

import net.wendal.nutzbook.BuildConfig;

public class AppController extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = this;

            // 配置全局异常捕获
            if (!BuildConfig.DEBUG) {
                Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(this));
            }

            // 配置友盟更新日志
            UpdateConfig.setDebug(BuildConfig.DEBUG);

            try {
                MiPushClient.registerPush(this, "2882303761517440917", "5841744096917");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
