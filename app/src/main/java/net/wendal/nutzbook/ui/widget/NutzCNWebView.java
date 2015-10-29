package net.wendal.nutzbook.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import net.wendal.nutzbook.ui.listener.NutzCNWebViewClient;

public class NutzCNWebView extends WebView {

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public NutzCNWebView(Context context) {
        super(context);
        setWebViewClient(NutzCNWebViewClient.with(context));
    }

    public NutzCNWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewClient(NutzCNWebViewClient.with(context));
    }

    public NutzCNWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWebViewClient(NutzCNWebViewClient.with(context));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NutzCNWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWebViewClient(NutzCNWebViewClient.with(context));
    }

    public void loadRenderedContent(String data) {
        loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt){
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback(){
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback){
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public static interface OnScrollChangedCallback{
        void onScroll(int l, int t);
    }

}
