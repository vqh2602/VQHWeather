package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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

    public void onClickatcv(View v) {
        GoToURL("https://antoancovid.vn/");
    }
    public void onClickbyt(View v) {
        GoToURL("https://ncov.moh.gov.vn/");
    }
    public void onClickvtc(View v) {
        GoToURL("https://vtc.vn/");
    }
    public void onClickapi(View v) {
        GoToURL("https://disease.sh/");
    }

    private void GoToURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}