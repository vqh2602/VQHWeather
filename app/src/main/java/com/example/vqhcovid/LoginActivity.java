package com.example.vqhcovid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class LoginActivity extends AppCompatActivity {

    ImageButton imageButton_Login;
    Button button_skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // full màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        // check ddang nhap


        imageButton_Login = findViewById(R.id.imageButton_Login);
        button_skip = findViewById(R.id.button_skip);

        imageButton_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
//        AccountAuthService service = AccountAuthManager.getService(LoginActivity.this, authParams);
//                Task<AuthAccount> task = service.silentSignIn();

//                task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
//                    @Override
//                    public void onSuccess(AuthAccount authAccount) {
//                        // Obtain the user's ID information.
//                        Log.i("a", "displayName:" + authAccount.getDisplayName());
//                        // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
//                        Log.i("a", "accountFlag:" + authAccount.getAccountFlag());
//
//                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    }
//                });
//
//                task.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        // The sign-in failed. Try to sign in explicitly using getSignInIntent().
//                        if (e instanceof ApiException) {
//                            ApiException apiException = (ApiException) e;
//                            Log.i("lgfaile", "sign failed status:" + apiException.getStatusCode());
//                            Toast.makeText(LoginActivity.this,"loi dang nhap",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });


                AccountAuthParams authParams = new AccountAuthParamsHelper
                        (AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
                AccountAuthService service = AccountAuthManager.getService(LoginActivity.this, authParams);
                startActivityForResult(service.getSignInIntent(), 8888);

//                AccountAuthParams authParams = new AccountAuthParamsHelper
//                        (AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().setProfile().setIdToken().setMobileNumber().createParams();
//                AccountAuthService service = AccountAuthManager.getService(LoginActivity.this, authParams);
//                startActivityForResult(service.getSignInIntent(), 8888);
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("skip", "skip");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result and obtain an ID token from AuthAccount.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and ID token are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
//                Log.i("LoginActivity", "idToken:" + authAccount.getIdToken());
//                // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
//                Log.i("LoginActivity", "accountFlag:"+ authAccount.getFamilyName () + authAccount.getGivenName());
//                // lấy tên
//
//                Log.i("LoginActivitymg", "img:" + authAccount.getAccountFlag());
                //TRUYEN DL
                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("Displayname", authAccount.getDisplayName());
                intent.putExtra("fullname", authAccount.getFamilyName ()+" " + authAccount.getGivenName());
                intent.putExtra("Imangeurl", authAccount.getAvatarUriString());
                startActivity(intent);
                finish();
            } else {
                // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                Log.e("LoginActivity", "sign in failed : " +((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }
}