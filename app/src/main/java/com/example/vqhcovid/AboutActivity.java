package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    TextView textView_phienban;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView_phienban = findViewById(R.id.textView_phienban);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_about)); //status bar or the time bar at the top

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        textView_phienban.setText(getResources().getString(R.string.phienban) + versionName);
    }
    public void onClickcsbm(View v) {
//        GoToURL("https://www.privacypolicies.com/live/a6b8d74b-5294-472f-8de7-73cf8ab31b8d");
        Intent intent = new Intent(AboutActivity.this, WebviewActivity.class);
        intent.putExtra("link", "https://www.privacypolicies.com/live/a6b8d74b-5294-472f-8de7-73cf8ab31b8d");
        startActivity(intent);
    }
    public void onClickweb(View v) {


        GoToURL("https://vqhcovid.herokuapp.com/");
    }


    private void GoToURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}