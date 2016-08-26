package net.wendal.nutzbook.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.wendal.nutzbook.R;
import net.wendal.nutzbook.model.api.ApiClient;
import net.wendal.nutzbook.model.entity.TabType;
import net.wendal.nutzbook.storage.LoginShared;
import net.wendal.nutzbook.storage.SettingShared;
import net.wendal.nutzbook.storage.TopicShared;
import net.wendal.nutzbook.ui.listener.NavigationFinishClickListener;
import net.wendal.nutzbook.ui.widget.EditorBarHandler;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewTopicActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {

    @Bind(R.id.new_topic_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.new_topic_spn_tab)
    protected Spinner spnTab;

    @Bind(R.id.new_topic_edt_title)
    protected EditText edtTitle;

    @Bind(R.id.editor_bar_layout_root)
    protected ViewGroup editorBar;

    @Bind(R.id.new_topic_edt_content)
    protected EditText edtContent;

    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
        toolbar.inflateMenu(R.menu.new_topic);
        toolbar.setOnMenuItemClickListener(this);

        dialog = new MaterialDialog.Builder(this)
                .content("正在发布中...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        // 创建EditorBar
        new EditorBarHandler(this, editorBar, edtContent);

        // 载入草稿
        if (SettingShared.isEnableNewTopicDraft(this)) {
            spnTab.setSelection(TopicShared.getNewTopicTabPosition(this));
            edtContent.setText(TopicShared.getNewTopicContent(this));
            edtContent.setSelection(edtContent.length());
            edtTitle.setText(TopicShared.getNewTopicTitle(this));
            edtTitle.setSelection(edtTitle.length()); // 这个必须最后调用
        }

        //this.registerForContextMenu(findViewById(R.id.new_topic_edt_content));
        mClipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    }


    public ClipboardManager mClipboard;
    final static int MENU_COPY = 1;
    final static int MENU_PASTE =2;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.add(Menu.NONE, MENU_COPY, Menu.NONE, "复制");
        menu.add(Menu.NONE, MENU_PASTE, Menu.NONE, "粘贴");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PASTE :
                if (!mClipboard.hasPrimaryClip()) {
                    Toast.makeText(this, "剪贴板里面没内容", Toast.LENGTH_SHORT);
                    return true;
                }
                ClipData clipData = mClipboard.getPrimaryClip();
                int count = clipData.getItemCount();
                if (count == 0)
                    return true;
                ClipData.Item t = clipData.getItemAt(0);
                StringBuilder sb = new StringBuilder(edtContent.getText());
                sb.insert(edtContent.getSelectionStart(), t.coerceToText(this));
                edtContent.setText(sb.toString(), TextView.BufferType.EDITABLE);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 实时保存草稿
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (SettingShared.isEnableNewTopicDraft(this)) {
            TopicShared.setNewTopicTabPosition(this, spnTab.getSelectedItemPosition());
            TopicShared.setNewTopicTitle(this, edtTitle.getText().toString());
            TopicShared.setNewTopicContent(this, edtContent.getText().toString());
        }
    }

    /**
     * 发送逻辑
     */

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                if (edtTitle.length() < 10) {
                    edtTitle.requestFocus();
                    Toast.makeText(this, "标题要求10字以上", Toast.LENGTH_SHORT).show();
                } else if (edtContent.length() == 0) {
                    edtContent.requestFocus();
                    Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    TabType tab = getTabByPosition(spnTab.getSelectedItemPosition());
                    String title = edtTitle.getText().toString().trim();
                    String content = edtContent.getText().toString();
                    if (SettingShared.isEnableTopicSign(this)) { // 添加小尾巴
                        content += "\n\n" + SettingShared.getTopicSignContent(this);
                    }
                    newTipicAsyncTask(tab, title, content);
                }
                return true;
            default:
                return false;
        }
    }

    private TabType getTabByPosition(int position) {
        switch (position) {
            case 0:
                return TabType.ask;
            case 1:
                return TabType.share;
            case 2:
                return TabType.job;
            case 3:
                return TabType.nb;
            default:
                return TabType.ask;
        }
    }

    private void newTipicAsyncTask(TabType tab, String title, String content) {
        dialog.show();
        ApiClient.service.newTopic(LoginShared.getAccessToken(this), tab, title, content, new Callback<Void>() {

            @Override
            public void success(Void nothing, Response response) {
                dialog.dismiss();
                // 清除草稿 TODO 由于保存草稿的动作在onPause中，并且保存过程是异步的，因此保险起见，优先清除控件数据
                spnTab.setSelection(0);
                edtTitle.setText(null);
                edtContent.setText(null);
                TopicShared.clear(NewTopicActivity.this);
                // 结束当前并提示
                finish();
                Toast.makeText(NewTopicActivity.this, "话题发布成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                    showAccessTokenErrorDialog();
                } else {
                    Toast.makeText(NewTopicActivity.this, R.string.network_faild, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void showAccessTokenErrorDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.access_token_error_tip)
                .positiveText(R.string.confirm)
                .show();
    }
    @Override
    public String name() {
        return "发新贴";
    }
}
