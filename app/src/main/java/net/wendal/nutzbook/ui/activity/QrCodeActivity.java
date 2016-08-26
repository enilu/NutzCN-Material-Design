package net.wendal.nutzbook.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import net.wendal.nutzbook.R;
import net.wendal.nutzbook.ui.listener.NavigationFinishClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QrCodeActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener {

    @Bind(R.id.qrcode_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.qrcode_qr_view)
    protected QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));

        qrCodeReaderView.setOnQRCodeReadListener(this);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent intent = new Intent();
        intent.putExtra("qrcode", text);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String name() {
        return "二维码界面";
    }
}
