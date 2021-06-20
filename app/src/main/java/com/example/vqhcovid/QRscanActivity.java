package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class QRscanActivity extends AppCompatActivity {

    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;

    TextView textView_Qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        textView_Qr = findViewById(R.id.textView_Qr);
    }
    public void newViewBtnClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    DEFAULT_VIEW);
        }
//        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
    }

    // 3. Override the onRequestPermissionsResult method
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFAULT_VIEW) {
            //After applying for permission, call the DefaultView scan code interface
            ScanUtil.startScan(QRscanActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
        }

    }

    // 3. Rewrite the onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //When the scan code page is over, process the scan code result
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        //From the data returned from onActivityResult, use ScanUtil.RESULT as the key value to get the HmsScan return value
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
//                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                    textView_Qr.setText(((HmsScan) obj).getOriginalValue());
                    String s = ((HmsScan) obj).getOriginalValue().substring(0,4);
                    if(s.equals("http")){
                        Toast.makeText(this, "Bạn sẽ được chuyển hướng sau 3 giây", Toast.LENGTH_SHORT).show();
                        openurl(((HmsScan) obj).getOriginalValue());
                    }else {
                        Toast.makeText(this, "Hãy quét các mã Qr có hỗ trợ KBYT để KBYT", Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }
        }
    }

    private void openurl(String s){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(s);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        },3000);
    }
}