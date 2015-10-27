package com.wgs.picker.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by w.gs on 2015/7/15.
 */
public abstract class BasePopupWindow extends PopupWindow {

    protected Context mContext;

    protected View mContentView;

    public BasePopupWindow(Context context) {
        super(context);
        setFocusable(true);
        mContext = context;
        onCreate();
        init();
    }


    protected abstract void onCreate();


    private void init() {
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    protected void setContentView(int id, int width, int height) {
        mContentView = LayoutInflater.from(mContext).inflate(id, null);
        super.setContentView(mContentView);
        setWidth(width);
        setHeight(height);
    }


    protected View findViewById(int id) {
        if (getContentView() == null) {
            throw new RuntimeException(this.getClass().getSimpleName() + " have not set content view!");
        }
        return getContentView().findViewById(id);
    }
}
