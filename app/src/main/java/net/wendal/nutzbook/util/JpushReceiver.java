package net.wendal.nutzbook.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import net.wendal.nutzbook.ui.activity.TopicActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class JpushReceiver extends android.content.BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.i("jpush", intent.toString());
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.i("jpush", type);
            try {
                TopicActivity.open(context, new JSONObject(type).getString("topic_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
