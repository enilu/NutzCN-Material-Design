package net.wendal.nutzbook.ui.listener;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.wendal.nutzbook.model.api.ApiClient;
import net.wendal.nutzbook.ui.activity.TopicActivity;
import net.wendal.nutzbook.ui.activity.UserDetailActivity;
import net.wendal.nutzbook.util.ShipUtils;

public class NutzCNWebViewClient extends WebViewClient {

    private volatile static NutzCNWebViewClient singleton;

    public static NutzCNWebViewClient with(Context context) {
        if (singleton == null) {
            synchronized (NutzCNWebViewClient.class) {
                if (singleton == null) {
                    singleton = new NutzCNWebViewClient(context);
                }
            }
        }
        return singleton;
    }

    private final Context context;

    protected NutzCNWebViewClient(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.startsWith(ApiClient.MAIN_HOST + ApiClient.URI_PREFIX_USER)) { // 用户主页协议
            UserDetailActivity.open(context, url.substring((ApiClient.MAIN_HOST + ApiClient.URI_PREFIX_USER).length()));
        } else if (url.startsWith(ApiClient.MAIN_HOST + ApiClient.URI_PREFIX_TOPIC)) { // 话题主页协议
            TopicActivity.open(context, url.substring((ApiClient.MAIN_HOST + ApiClient.URI_PREFIX_TOPIC).length()));
        } else { // 其他连接
            ShipUtils.openInBrowser(context, url);
        }
        return true;
    }
}
