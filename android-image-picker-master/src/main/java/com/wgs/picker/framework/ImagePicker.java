package com.wgs.picker.framework;

import android.content.Context;
import android.content.Intent;

import com.wgs.picker.ImagePickerActivity;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 图片选择器封装
 * Created by TuWei on 15/10/27.
 */
public class ImagePicker {

    public int max = 6;
    public Context context;
    public PickListener pickListener;

    /**
     * 新建一个imagepicker
     * @param context
     * @param listener
     * @param max 最多选取多少张,0代表无限制
     * @return
     */
    public static ImagePicker build(Context context, PickListener listener, int max) {
        ImagePicker picker = new ImagePicker();
        picker.context = context;
        picker.pickListener = listener;
        picker.max = max;
        return picker;
    }

    public void startActivity() {
        EventBus.getDefault().postSticky(new OnPublishPickOptionsEvent(this));
        final Intent intent = new Intent(context, ImagePickerActivity.class);
        context.startActivity(intent);
    }

    public final static class OnPublishPickOptionsEvent {
        public final ImagePicker options;

        public OnPublishPickOptionsEvent(final ImagePicker options) {
            this.options = options;
        }
    }

    /**
     * 图片选择回调
     */
    public interface PickListener {
        void onPickedSuccessfully(final ArrayList<String> images);
        void onCancel();
    }
}
