package net.wendal.nutzbook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import net.wendal.nutzbook.R;
import net.wendal.nutzbook.model.api.ApiClient;
import net.wendal.nutzbook.model.entity.Author;
import net.wendal.nutzbook.model.entity.Reply;
import net.wendal.nutzbook.model.entity.Result;
import net.wendal.nutzbook.model.entity.TopicUpInfo;
import net.wendal.nutzbook.model.entity.TopicWithReply;
import net.wendal.nutzbook.storage.LoginShared;
import net.wendal.nutzbook.storage.SettingShared;
import net.wendal.nutzbook.ui.adapter.TopicAdapter;
import net.wendal.nutzbook.ui.listener.NavigationFinishClickListener;
import net.wendal.nutzbook.ui.listener.NutzCNWebViewClient;
import net.wendal.nutzbook.ui.widget.EditorBarHandler;
import net.wendal.nutzbook.ui.widget.NutzCNWebView;
import net.wendal.nutzbook.ui.widget.RefreshLayoutUtils;
import net.wendal.nutzbook.util.FormatUtils;
import net.wendal.nutzbook.util.ShipUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopicActivity2 extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, TopicAdapter.OnAtClickListener, Toolbar.OnMenuItemClickListener {

    public static void open(Context context, String topicId) {
        Intent intent = new Intent(context, TopicActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("topicId", topicId);
        context.startActivity(intent);
    }

    public static final String API = "nutz";

    @Bind(R.id.topic_layout_root)
    protected ViewGroup layoutRoot;

    @Bind(R.id.topic_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.topic_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    @Bind(R.id.topic_web_view)
    protected NutzCNWebView webView;

    @Bind(R.id.topic_fab_reply)
    protected FloatingActionButton fabReply;

    private PopupWindow replyWindow;
    private ReplyHandler replyHandler;

    private MaterialDialog dialog;

    private String topicId;
    private TopicWithReply topic;

    //话题模板
    String template = "file:///android_asset/nutzcn/topic.html";
    //用于标识是否渲染过web
    boolean rendered = false;
    //用户标识模板是否加载完成
    boolean loaded = false;
    //是否为刷新
    boolean canRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic2);
        ButterKnife.bind(this);
        topicId = getIntent().getStringExtra("topicId");

        setupWebView();

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
        toolbar.inflateMenu(R.menu.topic);
        toolbar.setOnMenuItemClickListener(this);


        // 创建回复窗口
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_reply_window, layoutRoot, false);
        replyHandler = new ReplyHandler(view);

        replyWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        replyWindow.setBackgroundDrawable(new ColorDrawable(0x01000000));
        replyWindow.setFocusable(true);
        replyWindow.setOutsideTouchable(true);
        replyWindow.setAnimationStyle(R.style.AppTheme_ReplyWindowAnim);
        // - END -

        dialog = new MaterialDialog.Builder(this)
                .content("请稍后...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        RefreshLayoutUtils.initOnCreate(refreshLayout, this);
        RefreshLayoutUtils.refreshOnCreate(refreshLayout, this);
    }

    void setupWebView(){
        //控制fabReply 显示/隐藏
        webView.setOnScrollChangedCallback(new NutzCNWebView.OnScrollChangedCallback() {
            int lastT = 0;

            @Override
            public void onScroll(int l, int t) {
                //向下滑动, 隐藏
                if (t - lastT >= 0) {
                    if (fabReply.isVisible()) {
                        if (t - lastT > 50) {
                            fabReply.hide();
                            lastT = t;
                        }
                    } else {
                        lastT = t;
                    }
                } else {
                    if (!fabReply.isVisible()) {
                        if (t - lastT < -50) {
                            fabReply.show();
                            lastT = t;
                        }
                    } else {
                        lastT = t;
                    }
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new TopicWebViewClient(this));
        webView.addJavascriptInterface(new TopicInterface(), API);
        webView.loadUrl(template);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_in_browser:
                ShipUtils.openInBrowser(this, "https://nutz.cn/yvr/t/" + topicId);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRefresh() {
        ApiClient.service.getTopic(topicId, true, new Callback<Result<TopicWithReply>>() {

            @Override
            public void success(Result<TopicWithReply> result, Response response) {
                if (!isFinishing()) {
                    topic = result.getData();
                    //模板加载完成,且没渲染
                    if((loaded && !rendered) || canRefresh){
                        rendered = true;
                        canRefresh = true;
                        webView.loadUrl("javascript:renderData(" + new Gson().toJson(topic) + ");");
                    }
                    //从js里停止
                    //refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (!isFinishing()) {
                    if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                        Toast.makeText(TopicActivity2.this, R.string.topic_not_found, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TopicActivity2.this, R.string.data_load_faild, Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.setRefreshing(false);
                }
            }

        });
    }

    @Override
    public void onAt(String loginName) {
        replyHandler.edtContent.getText().insert(replyHandler.edtContent.getSelectionEnd(), " @" + loginName + " ");
        replyWindow.showAtLocation(layoutRoot, Gravity.BOTTOM, 0, 0);
    }

    @OnClick(R.id.topic_fab_reply)
    protected void onBtnReplyClick() {
        if (topic != null) {
            if (TextUtils.isEmpty(LoginShared.getAccessToken(this))) {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_login_tip)
                        .positiveText(R.string.login)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {

                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                startActivity(new Intent(TopicActivity2.this, LoginActivity.class));
                            }

                        })
                        .show();
            } else {
                replyWindow.showAtLocation(layoutRoot, Gravity.BOTTOM, 0, 0);
            }
        }
    }

    //==============
    // 回复框逻辑处理
    //==============

    protected class ReplyHandler {

        @Bind(R.id.editor_bar_layout_root)
        protected ViewGroup editorBar;

        @Bind(R.id.reply_window_edt_content)
        protected EditText edtContent;

        protected ReplyHandler(View view) {
            ButterKnife.bind(this, view);
            new EditorBarHandler(TopicActivity2.this, editorBar, edtContent); // 创建editorBar
        }

        /**
         * 关闭
         */
        @OnClick(R.id.reply_window_btn_tool_close)
        protected void onBtnToolCloseClick() {
            replyWindow.dismiss();
        }

        /**
         * 发送
         */
        @OnClick(R.id.reply_window_btn_tool_send)
        protected void onBtnToolSendClick() {
            if (edtContent.length() == 0) {
                Toast.makeText(TopicActivity2.this, "内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                String content = edtContent.getText().toString();
                if (SettingShared.isEnableTopicSign(TopicActivity2.this)) { // 添加小尾巴
                    content += "\n\n" + SettingShared.getTopicSignContent(TopicActivity2.this);
                }
                replyTopicAsyncTask(content);
            }
        }

        private void replyTopicAsyncTask(final String content) {
            dialog.show();
            ApiClient.service.replyTopic(LoginShared.getAccessToken(TopicActivity2.this), topicId, content, null, new Callback<Map<String, String>>() {

                @Override
                public void success(Map<String, String> result, Response response) {
                    Log.i("reply", result.toString());
                    dialog.dismiss();
                    // 本地创建一个回复对象
                    Reply reply = new Reply();
                    reply.setId(result.get("reply_id"));
                    Author author = new Author();
                    author.setLoginName(LoginShared.getLoginName(TopicActivity2.this));
                    author.setAvatarUrl(LoginShared.getAvatarUrl(TopicActivity2.this));
                    reply.setAuthor(author);
                    reply.setContent(FormatUtils.renderMarkdown2(content));
                    reply.setCreateAt(new DateTime());
                    reply.setUps(new ArrayList<String>());
                    topic.getReplies().add(reply);
                    replyWindow.dismiss();

                    webView.loadUrl("javascript:addReply(" + new Gson().toJson(reply) + ");");

                    // 清空回复框内容
                    edtContent.setText(null);
                    // 提示
                    Toast.makeText(TopicActivity2.this, "发送成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    dialog.dismiss();
                    if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                        new MaterialDialog.Builder(TopicActivity2.this)
                                .content(R.string.access_token_error_tip)
                                .positiveText(R.string.confirm)
                                .show();
                    } else {
                        Toast.makeText(TopicActivity2.this, R.string.network_faild, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

    //点赞
    private void upTopicAsyncTask(final String replyId) {
        ApiClient.service.upTopic(LoginShared.getAccessToken(this), replyId, new Callback<TopicUpInfo>() {

            @Override
            public void success(TopicUpInfo info, Response response) {
                webView.loadUrl("javascript:likeCallback(true, '" + replyId + "')");
            }

            @Override
            public void failure(RetrofitError error) {
                webView.loadUrl("javascript:likeCallback(false, '" + replyId + "')");
                if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                    new MaterialDialog.Builder(TopicActivity2.this)
                            .content(R.string.access_token_error_tip)
                            .positiveText(R.string.confirm)
                            .show();
                } else {
                    Toast.makeText(TopicActivity2.this, "网络访问失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //===================
    // WebView interface
    //===================

    class TopicWebViewClient extends NutzCNWebViewClient{
        protected TopicWebViewClient(Context context) {
            super(context);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //模板加载完毕
            if(url.equals(template)){
                loaded = true;
                //有数据, 且没渲染
                if(topic != null && !rendered){
                    //render
                    rendered = true;
                    canRefresh = true;
                    webView.loadUrl("javascript:renderData(" + new Gson().toJson(topic) + ");");
                }
            }
            super.onPageFinished(view, url);
        }
    }

    class TopicInterface{

        //显示提示
        @JavascriptInterface
        public void toast(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TopicActivity2.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        //用户名
        @JavascriptInterface
        public String loginName() {
            return LoginShared.getLoginName(TopicActivity2.this);
        }

        //用户id
        @JavascriptInterface
        public String loginId() {
            return LoginShared.getId(TopicActivity2.this);
        }

        //回复评论
        @JavascriptInterface
        public void replyComment(final String username, String replyId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onAt(username);
                }
            });
        }

        //查看用户详情
        @JavascriptInterface
        public void goUserInfo(final String username) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UserDetailActivity.open(TopicActivity2.this, username);
                }
            });
        }

        @JavascriptInterface
        public void like(final String replyId){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upTopicAsyncTask(replyId);
                }
            });
        }

        @JavascriptInterface
        public String getNearDateStr(String originStr){
            try{
                if("undefined".equals(originStr)){
                    return FormatUtils.getRecentlyTimeText(new DateTime());
                }
                return FormatUtils.getRecentlyTimeText(new DateTime(originStr));
            }catch (Exception e){
                e.printStackTrace();
                return "未知";
            }
        }

        @JavascriptInterface
        public void stopRefresh(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            });
        }

    }



}
