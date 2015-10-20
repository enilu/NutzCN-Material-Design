package net.wendal.nutzbook.ui.listener;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

    private NutzCNWebViewClient(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.startsWith("https://nutz.cn/yvr/user/")) { // 用户主页协议
            UserDetailActivity.open(context, url.substring("https://nutz.cn/yvr/user/".length()));
        } else if (url.startsWith("https://nutz.cn/yvr/t/")) { // 话题主页协议
            TopicActivity.open(context, url.substring("https://nutz.cn/yvr/t/".length()));
        } else { // 其他连接
            ShipUtils.openInBrowser(context, url);
        }
        return true;
    }

}
