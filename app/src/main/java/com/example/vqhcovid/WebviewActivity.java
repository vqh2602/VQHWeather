package com.example.vqhcovid;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class WebviewActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView)findViewById(R.id.wwiew);

        Intent intent = getIntent();

//        String link = intent.getStringExtra("link tin tuc");

        String link = intent.getStringExtra("link");

//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
      //  webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(link);
    }
}