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
        String extras = bundle == null ? "{}" : bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (extras == null)
            extras = "{}";
        Log.i("jpush", extras);
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            try {
                String topic_id = new JSONObject(extras).getString("topic_id");
                if (topic_id != null)
                    TopicActivity.open(context, topic_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            try {
                JSONObject json = new JSONObject(extras);
                String topic_id = json.getString("topic_id");
                String action = json.getString("action");
                if (topic_id != null && "open_topic".equals(action))
                    TopicActivity.open(context, topic_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
