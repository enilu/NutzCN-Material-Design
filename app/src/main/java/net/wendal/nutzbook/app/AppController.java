package net.wendal.nutzbook.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.market.sdk.*;

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

            try {
                MiPushClient.registerPush(this, "2882303761517440917", "5841744096917");
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        switch (updateStatus) {
                            case UpdateStatus.STATUS_UPDATE:
                                XiaomiUpdateAgent.arrange();
                                break;
                            case UpdateStatus.STATUS_NO_UPDATE:
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "暂无更新", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    }
                });
                XiaomiUpdateAgent.update(this);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
