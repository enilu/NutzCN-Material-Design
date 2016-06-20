package net.wendal.nutzbook.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.wendal.nutzbook.R;
import net.wendal.nutzbook.ui.listener.NavigationFinishClickListener;
import net.wendal.nutzbook.ui.widget.NutzCNWebView;
import net.wendal.nutzbook.util.FormatUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarkdownPreviewActivity extends BaseActivity {

    @Bind(R.id.markdown_preview_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.markdown_preview_web_view)
    protected NutzCNWebView cnodeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown_preview);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));

        cnodeWebView.loadRenderedContent(FormatUtils.renderMarkdown(getIntent().getStringExtra("markdownText")));
    }

    @Override
    public void onBackPressed() {
        if (cnodeWebView.canGoBack()) {
            cnodeWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public String name() {
        return "预览界面";
    }
}
