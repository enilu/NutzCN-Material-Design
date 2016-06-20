package net.wendal.nutzbook.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.xiaomi.mistatistic.sdk.MiStatInterface;


public abstract class BaseActivity extends AppCompatActivity {

    public abstract String name();

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, name());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

}
