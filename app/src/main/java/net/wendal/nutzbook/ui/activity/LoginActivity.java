package net.wendal.nutzbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.wendal.nutzbook.R;
import net.wendal.nutzbook.model.api.ApiClient;
import net.wendal.nutzbook.model.entity.LoginInfo;
import net.wendal.nutzbook.storage.LoginShared;
import net.wendal.nutzbook.ui.listener.NavigationFinishClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends BaseActivity {

    private static final int REQUEST_QRCODE = 100;

    @Bind(R.id.login_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.login_edt_access_token)
    protected MaterialEditText edtAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
    }

    @OnClick(R.id.login_btn_login)
    protected void onBtnLoginClick() {
        if (edtAccessToken.getText().length() < 1) {
            edtAccessToken.setError("令牌格式错误");
        } else {

            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .content("正在登录中...")
                    .progress(true, 0)
                    .build();
            dialog.show();

            final String accessToken = edtAccessToken.getText().toString();

            ApiClient.service.accessToken(accessToken, new Callback<LoginInfo>() {

                @Override
                public void success(LoginInfo loginInfo, Response response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        LoginShared.login(LoginActivity.this, accessToken, loginInfo);
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                            edtAccessToken.setError(getString(R.string.access_token_error));
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.network_faild, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            });

        }
    }

    @OnClick(R.id.login_btn_qrcode)
    protected void onBtnQrcodeClick() {
        startActivityForResult(new Intent(this, QrCodeActivity.class), REQUEST_QRCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QRCODE && resultCode == RESULT_OK) {
            edtAccessToken.setText(data.getStringExtra("qrcode"));
            edtAccessToken.setSelection(edtAccessToken.length());
        }
    }
}
