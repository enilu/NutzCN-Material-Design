package net.wendal.nutzbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import net.wendal.nutzbook.BuildConfig;
import net.wendal.nutzbook.R;
import net.wendal.nutzbook.util.HandlerUtils;

import cn.jpush.android.api.JPushInterface;

public class LaunchActivity extends BaseActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        setContentView(R.layout.activity_launch);
        HandlerUtils.postDelayed(this, 2000);
    }

    @Override
    public void run() {
        if (!isFinishing()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
