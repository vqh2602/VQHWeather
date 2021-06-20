package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AccountActivity extends AppCompatActivity {

    TextView textView_uid,textView_Nickname,textView_countryCode;
    ImageView imageView_uri;
    Button button_lg;
    String displayname_check;

    private AccountAuthService mAuthManager;
    private AccountAuthParams mAuthParam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        anhxa();
        // full màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        nhandulieuintent();

        //check
        if(displayname_check != null){
            button_lg.setText("LogOut");
            button_lg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });

        }
        else {
            button_lg.setText("LogIn");
            button_lg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccountActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }

    }

    private void anhxa(){
        textView_uid= findViewById(R.id.textView_uid);
        textView_Nickname= findViewById(R.id.textView_Nickname);
        imageView_uri= findViewById(R.id.imageView_uri);
        button_lg= findViewById(R.id.button_lg);
    }

    private void nhandulieuintent()  {
        Intent intent = getIntent();
        textView_uid.setText("FullName: "+intent.getStringExtra("fullname"));
        textView_Nickname.setText("Nickname: "+intent.getStringExtra("Displayname"));

        new DownloadImageFromInternet((ImageView) findViewById(R.id.imageView_uri)).execute(intent.getStringExtra("Imangeurl"));
               displayname_check = intent.getStringExtra("Displayname");


    }


    // get url image
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage=BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


    //logout
    private void logout(){
        // tạo servier
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
        mAuthManager = AccountAuthManager.getService(AccountActivity.this, mAuthParam);
        // đăng xuất
        Task<Void> signOutTask = mAuthManager.signOut();
        signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                //Processing after the sign-out.
                Log.i("MainActivitylogout", "signOut complete");
                Toast.makeText(AccountActivity.this,"Đã Đăng Xuất" ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AccountActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}