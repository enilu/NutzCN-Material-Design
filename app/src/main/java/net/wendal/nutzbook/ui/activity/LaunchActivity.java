package net.wendal.nutzbook.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.wendal.nutzbook.BuildConfig;
import net.wendal.nutzbook.R;
import net.wendal.nutzbook.util.HandlerUtils;

import java.util.Random;

import cn.jpush.android.api.JPushInterface;

public class LaunchActivity extends BaseActivity implements Runnable {

    ImageView image;
    TextView title;
    View foreMask;
    View logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        setContentView(R.layout.activity_launch);

        setupSplashImage();
    }

    void setupSplashImage(){
        image = (ImageView)findViewById(R.id.image);
        title = (TextView)findViewById(R.id.title);
        foreMask = findViewById(R.id.foreMask);
        logo = findViewById(R.id.logo);

        int[] sps = new int[]{
                R.drawable.sp1,
                R.drawable.sp2,
                R.drawable.sp3,
                R.drawable.sp4,
                R.drawable.sp5
        };

        int index = new Random().nextInt(sps.length);
        Picasso.with(LaunchActivity.this).load(sps[index]).into(image);
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(800);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foreMask.setAlpha(0.0f);
                HandlerUtils.postDelayed(LaunchActivity.this, 1200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        foreMask.startAnimation(animation);
    }

    @Override
    public void run() {
        if (!isFinishing()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
