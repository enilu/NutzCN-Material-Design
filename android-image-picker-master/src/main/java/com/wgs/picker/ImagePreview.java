package com.wgs.picker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgs.picker.framework.DateTimeUtil;
import com.wgs.picker.framework.Density;
import com.wgs.picker.framework.ImageUtil;
import com.wgs.picker.framework.LocalImageLoader;

import java.io.File;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by w.gs on 2015/7/22.
 */
public class ImagePreview extends Activity implements View.OnClickListener {

    private PhotoView iv_img;

    private ImageButton ibtn_back;

    private Button btn_detail,btn_close;

    private TextView tv_detail;


    private File mCurrentFile;

    private Handler mHanler = new Handler();

    private View fl_top, ll_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        fl_top = findViewById(R.id.fl_top);

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);

        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);

        btn_detail = (Button) findViewById(R.id.btn_detail);
        btn_detail.setOnClickListener(this);
        ll_detail = findViewById(R.id.ll_detail);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (Density.getSceenWidth(this) * 0.8),
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER;
        ll_detail.setLayoutParams(params);


        tv_detail = (TextView) findViewById(R.id.tv_detail);

        iv_img = (PhotoView) findViewById(R.id.iv_img);

        mCurrentFile = (File) getIntent().getSerializableExtra("img_file");
        String img_path = mCurrentFile.getAbsolutePath();
        if (!TextUtils.isEmpty(img_path)) {
            LocalImageLoader.getInstance()
                    .displaySingleImage(img_path, iv_img, Density.getSceenWidth(this), Density.getSceenHeight(this));

        }

        mHanler.postDelayed(new Runnable() {
            public void run() {
                fl_top.setVisibility(View.GONE);
            }
        }, 1000);

    }


    private int mCount = 1;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCount++ < 10000) {
            if (mCount % 2 == 0) {//减少调用的频率
                mHanler.removeCallbacks(mHideViewRunnalbe);
                if (!fl_top.isShown()) {
                    fl_top.setVisibility(View.VISIBLE);
                }
                mHanler.postDelayed(mHideViewRunnalbe, 1500);

            }
        } else {
            mCount = 0;
        }

        return super.dispatchTouchEvent(ev);
    }



    private Runnable mHideViewRunnalbe = new Runnable() {
        @Override
        public void run() {
            fl_top.setVisibility(View.GONE);
        }
    };

    public void onClick(View v) {
        if (v == ibtn_back) {
            finish();
        } else if (v == btn_detail) {
            if (ll_detail.isShown()) {
                ll_detail.setVisibility(View.GONE);
            } else {
                ll_detail.setVisibility(View.VISIBLE);
            }

            int[] img_size = ImageUtil.getImageSize(mCurrentFile.getAbsolutePath());

            StringBuilder sb = new StringBuilder();

            sb.append("文件名：").append(mCurrentFile.getName())
                    .append("\n尺寸：").append(img_size[0]).append("*").append(img_size[1])
                    .append("\n大小：").append(formatSize(img_size[0] * img_size[1]))
                    .append("\n修改时间：").append(DateTimeUtil.formatDateTime(mCurrentFile.lastModified()));


            try {
                ExifInterface exif = new ExifInterface(mCurrentFile.getAbsolutePath());
                if (exif != null) {
                    String date_time = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    if (date_time != null) {
                        sb.append("\n拍摄时间：").append(date_time)
                                .append("\n旋转角度：").append(ImageUtil.getExifOrientation(mCurrentFile.getAbsolutePath()))
                                .append("\n设备：").append(exif.getAttribute(ExifInterface.TAG_MODEL))
                                .append("\n焦距：").append(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH))
                                .append("\n光圈：").append(exif.getAttribute(ExifInterface.TAG_APERTURE))
                                .append("\n曝光时间：").append(exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME))
                                .append("\nISO：").append(exif.getAttribute(ExifInterface.TAG_ISO))
                                .append("\n制造商：").append(exif.getAttribute(ExifInterface.TAG_MAKE));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append("\n图片路径：").append(mCurrentFile.getAbsolutePath());
            tv_detail.setText(sb.toString());
        }else if(v==btn_close){
            ll_detail.setVisibility(View.GONE);
        }
    }

    private String formatSize(double len) {

        if (len < 1024) {
            return len + "Byte";
        }

        if (len < 1024 * 1024) {
            return String.format("%.2f", len / 1024.0) + "K";
        } else {
            return String.format("%.2f", len / (1024 * 1024)) + "M";
        }
    }
}
