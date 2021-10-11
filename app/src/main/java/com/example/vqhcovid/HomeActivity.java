package com.example.vqhcovid;

import static com.example.vqhcovid.R.drawable.ic_toolbar_menu_white;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vqhcovid.weather.AdapterWeather7;
import com.example.vqhcovid.weather.Adapterweather24;
import com.example.vqhcovid.weather.Weather24;
import com.example.vqhcovid.weather.Weather7;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.DailyWeather;
import com.huawei.hms.kit.awareness.status.weather.HourlyWeather;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String CHANNEL_ID ="" ;
    private static final int NOTIFICATION_ID =0 ;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String displayname_check = "", skip = "", fullname, disname, imgurl;

    private static final String TAG = "weather";
    TextView textViewtemp, textViewweatherid, textViewcity, textViewwind, textViewhum, textViewrealtemp, textViewuv,
            textView_7, textView_24;
    ImageView imageView_background;
    TextClock textClock;
    LottieAnimationView imageViewwtid;

    ListView listview_24h;
    com.example.vqhcovid.weather.Adapterweather24 Adapterweather24;
    ArrayList<Weather24> list_24h;

    ListView listview_7day;
    AdapterWeather7 adapterWeather7;
    ArrayList<Weather7> list_7days;
    String troi = "";
    int imageUrl;
    private final int permissionRequestCode = 9999;

    Handler mHandler;
    private MyReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViewId();

        actionToolBar();

        viewSetup();

        //authentication Checking
        authenticationChecking();

        activities();


        // reset dữ liệu 15p / lần

        this.mHandler = new Handler();
        m_Runnable.run();

        // push lit
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.vqhcovid.ON_NEW_TOKEN");
        filter.addAction("com.example.vqhcovid.ON_MSG_RECEIVED");
        HomeActivity.this.registerReceiver(receiver, filter);
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            Toast.makeText(HomeActivity.this,"Đang làm mới dữ liệu . . .",Toast.LENGTH_SHORT).show();
            startGetWeathers();
           HomeActivity.this.mHandler.postDelayed(m_Runnable,900000);

        }

    };







    private void activities() {
        //FIXME add this into the thread when loading
        Toast.makeText(HomeActivity.this, "Đang tải dữ liệu . . .", Toast.LENGTH_LONG).show();

        if (isAllPermissionAllowed()) {
            // You can use the API that requires the permission.
            startGetWeathers();
        } else if (shouldShowRequestPermissionRationale(accessFineLocation)) {
           //TODO show UI to ask permission again
        } else if (shouldShowRequestPermissionRationale(accessCoarseLocation)){
            //TODO show UI to ask permission again
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            accessFineLocation,
                            accessCoarseLocation
                    },
                    permissionRequestCode);
        }
    }

    private void startGetWeathers() {
        if (!isGpsEnabled()) {
            showAlertWhenGpsNotEnabled();
        } else {
            getCurrentWeather();

//            getHourlyWeather();

            //FIXME this handler will dead in background mode. Fix it by using service, rx or do in Activity Life Cycle
            new Handler().postDelayed(this::getHourlyWeather, 2000);
            new Handler().postDelayed(this::getDailyWeather, 4000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).allMatch(i -> i == PackageManager.PERMISSION_GRANTED)){
                startGetWeathers();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void viewSetup() {
        navigationView.bringToFront();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toolbar, R.string.close_toolbar);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        //khai báo sự kiện onclick từng item menu ->public boolean onNavigationItemSelected
        navigationView.setNavigationItemSelectedListener(this);

        dayNightBackground();

        // xử lí kéo listview chồng lên scrollview
        listview_24h.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });

        listview_7day.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });

        // xửa lí adapter
        set24hourWeather();

        set7daysWeather();
    }

    private void authenticationChecking() {
        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
        AccountAuthService accountAuthService = AccountAuthManager.getService(HomeActivity.this, authParams);
        Task<AuthAccount> authAccountTask = accountAuthService.silentSignIn();

        authAccountTask.addOnSuccessListener(authAccount -> {
            // Obtain the user's ID information.
            Log.i(TAG, "displayName:" + authAccount.getDisplayName());
            // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
            Log.i(TAG, "accountFlag:" + authAccount.getAccountFlag());
            mapUserInfo(authAccount);

            fullname = authAccount.getFamilyName() + " " + authAccount.getGivenName();
            imgurl = authAccount.getAvatarUriString();
            disname = authAccount.getDisplayName();
            displayname_check = "ok";
        });

        authAccountTask.addOnFailureListener(e -> {
            // The sign-in failed. Try to sign in explicitly using getSignInIntent().
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                Log.i(TAG, "sign failed status:" + apiException.getStatusCode());
                Toast.makeText(HomeActivity.this, "Bạn chưa đăng nhập", Toast.LENGTH_LONG).show();

                displayname_check = "";
                Intent intent = getIntent();
                skip = intent.getStringExtra("skip");
                Log.i(TAG, "displayNameskip:" + skip);
                if (skip == null) {
                    Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }

            }
        });
    }

    //ham su kien thi an nut tro ve
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void actionToolBar() {
        // taoj thu vien ho tro
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //set icon
        toolbar.setNavigationIcon(ic_toolbar_menu_white);
        //toolbar.getNavigationIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

        //bat su kien onclick
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        toolbar.setNavigationIcon(ic_toolbar_menu_white);
    }

    private void bindViewId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        textViewtemp = findViewById(R.id.textViewtemp);
        textViewweatherid = findViewById(R.id.textViewweatherid);
        textViewcity = findViewById(R.id.textViewcity);
        textViewwind = findViewById(R.id.textViewwind);
        textViewhum = findViewById(R.id.textViewhum);
        imageViewwtid = findViewById(R.id.imageViewwtid);
        textViewrealtemp = findViewById(R.id.textViewrealtemp);
        textViewuv = findViewById(R.id.textViewuv);
        textClock = findViewById(R.id.textclock);
        imageView_background = findViewById(R.id.imageView_background);
        textView_7 = findViewById(R.id.textView_7);
        textView_24 = findViewById(R.id.textView_24);
        listview_24h = findViewById(R.id.listview_24h);
        listview_7day = findViewById(R.id.listview_7days);
//        listView = (ListView) findViewById(R.id.listview_menu);
    }

    // hàm sử lí sự kiện khi click vào menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home_main) {
            //check
            //check
            if (displayname_check != null) {
                goToAccountInformationScreen();
            } else {
                Toast.makeText(HomeActivity.this, "Bạn Chưa Đăng Nhập", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        } else if (id == R.id.tintuc_main) {
            //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(HomeActivity.this, InformationRssActivity.class);
            Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
            startActivity(intent);

        } else if (id == R.id.QR_main) {
            //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeActivity.this, QRscanActivity.class);
            startActivity(intent);

        } else if (id == R.id.About_main) {
            //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.sp_ins) {
            Toast.makeText(HomeActivity.this, "instagram", Toast.LENGTH_LONG).show();
            goToURL("https://www.instagram.com/vqh.26/");
        } else if (id == R.id.sp_fb) {
            Toast.makeText(HomeActivity.this, "facebook", Toast.LENGTH_LONG).show();
            goToURL("https://www.facebook.com/");
        } else if (id == R.id.sp_mail) {
            Toast.makeText(HomeActivity.this, "mail", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:vqh2602@gmail.com");
            intent.setData(data);
            startActivity(intent);
        }


        return true;
    }

    //open mang xa hoi
    private void goToURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //check du lieu intent
    private void mapUserInfo(AuthAccount authAccount) {
        //check
        if (authAccount.getDisplayName() != null) {
            Toast.makeText(HomeActivity.this, "Xin Chào: " + authAccount.getDisplayName(), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(HomeActivity.this, "Bạn chưa đăng nhập ", Toast.LENGTH_LONG).show();
        }
    }

    private void goToAccountInformationScreen() {
        //FIXME fix string
        Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
        intent.putExtra("Displayname", disname);
        intent.putExtra("fullname", fullname);
        intent.putExtra("Imangeurl", imgurl);
        startActivity(intent);
    }

    //check quyen vi tri
    private boolean isAllPermissionAllowed() {
        int permission_lc1 = ContextCompat.checkSelfPermission(this,
                accessFineLocation);
        int permission_lc2 = ContextCompat.checkSelfPermission(this,
                accessCoarseLocation);

        return (permission_lc1 == PackageManager.PERMISSION_GRANTED
                && permission_lc2 == PackageManager.PERMISSION_GRANTED);
    }

    private boolean isGpsEnabled() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showAlertWhenGpsNotEnabled() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //FIXME hardcode String
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Exit", (dialog, id) -> this.finish())
                .create()
                .show();
    }

    private void setImageWeather(int i) {
        // variable: Một biến để kiểm tra.
        switch (i) {
            case 1:
                // nắng ...

                if (checkDayNight()) {
                    textViewweatherid.setText("Trời nắng");
                    imageViewwtid.setAnimation(R.raw.sun);
                    imageViewwtid.playAnimation();
                } else {
                    textViewweatherid.setText("Trời trong");
                    imageViewwtid.setAnimation(R.raw.wnight);
                    imageViewwtid.playAnimation();
                }
                break;
            case 2:
                // mây ngắt quãng, nắng mờ...
                textViewweatherid.setText("Có mây");
//                imageViewwtid.setImageResource(R.drawable.partly_cloudy_day_50px_color);
                imageViewwtid.setAnimation(R.raw.partlycould);
                imageViewwtid.playAnimation();

                break;
            case 3:
                // nhieu mây ...
                textViewweatherid.setText("Nhiều mây");
//                imageViewwtid.setImageResource(R.drawable.cloud_50px_color);
                imageViewwtid.setAnimation(R.raw.clould);
                imageViewwtid.playAnimation();
                break;
            case 4:
                // sương ...
                textViewweatherid.setText("Sương");
//                imageViewwtid.setImageResource(R.drawable.dust_50px);
                imageViewwtid.setAnimation(R.raw.dust);
                imageViewwtid.playAnimation();
                break;
            case 5:
                // mưa ...
                textViewweatherid.setText("Có mưa");
//                imageViewwtid.setImageResource(R.drawable.rain_50px_color);
                if (checkDayNight()) {
                    imageViewwtid.setAnimation(R.raw.rain);
                    imageViewwtid.playAnimation();
                } else {
                    imageViewwtid.setAnimation(R.raw.rainynight);
                    imageViewwtid.playAnimation();
                }

                break;
            case 6:
                // dông ...
                textViewweatherid.setText("Có dông");
//                imageViewwtid.setImageResource(R.drawable.storm_50px_color);
                imageViewwtid.setAnimation(R.raw.strom);
                imageViewwtid.playAnimation();
                break;
            case 7:
                // có tuyết ...
                textViewweatherid.setText("Có tuyết");
//                imageViewwtid.setImageResource(R.drawable.snow_50px_color);
                imageViewwtid.setAnimation(R.raw.snow);
                imageViewwtid.playAnimation();
                break;
            case 8:
                // đóng băng ...
                textViewweatherid.setText("Băng");
//                imageViewwtid.setImageResource(R.drawable.icy_50px);
                imageViewwtid.setAnimation(R.raw.icy);
                imageViewwtid.playAnimation();
                break;
            case 9:
                // mưa và tuyết ...
                textViewweatherid.setText("Có mưa tuyết");
//                imageViewwtid.setImageResource(R.drawable.sleet_50px_color);
                imageViewwtid.setAnimation(R.raw.sleet);
                imageViewwtid.playAnimation();
                break;
            default:
                // Làm gì đó tại đây ...
        }
    }

    private int getIdWeather(int i) {
        int id = 0;
        if (i == 1 || i == 2 || i == 3 || i == 30 || i == 33) {
            id = 1;
        } else if (i == 4 || i == 5 || i == 6 || i == 34 || i == 37) {
            id = 2;
        } else if (i == 7 || i == 35 || i == 36 || i == 38) {
            id = 3;
        } else if (i == 11 || i == 43 || i == 44) {
            id = 4;
        } else if (i == 12 || i == 13 || i == 14 || i == 39 || i == 40 || i == 8) {
            id = 5;
        } else if (i == 15 || i == 16 || i == 17 || i == 18 || i == 41 || i == 42) {
            id = 6;
        } else if (i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 31) {
            id = 7;
        } else if (i == 24 || i == 25 || i == 26) {
            id = 8;
        } else if (i == 29) {
            id = 9;
        }

        return id;
    }

    private void setUV(int i) {
        // variable: Một biến để kiểm tra.
        switch (i) {
            case 0:
                // không có ...
                textViewuv.setText("UV: không có");
                break;
            case 1:
                // tia UV rất yếu.
                textViewuv.setText("UV: rất yếu");
                break;
            case 2:
                // UV yếu
                textViewuv.setText("UV: yếu");
                break;
            case 3:
                //  tia trung bình
                textViewuv.setText("UV: trung bình");
                break;
            case 4:
                // mạnh
                textViewuv.setText("UV: mạnh");
                break;
            case 5:
                // rất mạnh
                textViewuv.setText("UV: rất mạnh");
                break;
            default:
                // Làm gì đó tại đây ...
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentWeather() {
        // lấy thời tiết hiện tại
        Awareness.getCaptureClient(this).getWeatherByDevice()
                // Callback listener for execution success.
                .addOnSuccessListener(weatherStatusResponse -> {
                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
                    Situation situation = weatherSituation.getSituation();
                    // For more weather information, please refer to the API Reference of Awareness Kit.
                    String weatherInfoStr = "City:" + weatherSituation.getCity().getName() + "\n" +
                            "Weather id is " + situation.getWeatherId() + "\n" +
                            "CN Weather id is " + situation.getCnWeatherId() + "\n" +
                            "Temperature is " + situation.getTemperatureC() + "℃" +
                            "," + situation.getTemperatureF() + "℉" + "\n" +
                            "Wind speed is " + situation.getWindSpeed() + "km/h" + "\n" +
                            "Wind direction is " + situation.getWindDir() + "\n" +
                            "Humidity is " + situation.getHumidity() + "%" + "\n" +
                            "Readfeel is " + situation.getRealFeelC() + "\n" +
                            "Uv is " + situation.getUvIndex() + "\n" +
                            "Update is " + situation.getUpdateTime() + "\n";
                    Log.i(TAG, weatherInfoStr);


                    // xuwr lis dl
                    setImageWeather(getIdWeather(situation.getWeatherId()));
                    setUV(situation.getUvIndex());
                    String city = "City: " + weatherSituation.getCity().getName();
                    String hum = "Humidity: " + situation.getHumidity() + " %";
                    String wind = "Wind speed: " + situation.getWindSpeed() + " km/h";
                    String temp = situation.getTemperatureC() + "°";
                    String realtemp = "Cảm giác như: " + situation.getRealFeelC() + "°";
                    textViewcity.setText(city);
                    textViewhum.setText(hum);
                    textViewwind.setText(wind);
                    textViewtemp.setText(temp);
                    textViewrealtemp.setText(realtemp);

                    //set statusbar
                    displayNotification(temp,city,"Feel like: "+situation.getRealFeelC()+"°"
                            +"  | Wind: "+situation.getWindSpeed() + " km/h"
                            +"  | Humidity: "+situation.getHumidity() + " %");

                })
                // Callback listener for execution failure.
                .addOnFailureListener(e -> Log.e(TAG, "get weather failed: " + e.getLocalizedMessage()));
    }

    @SuppressLint("MissingPermission")
    private void getHourlyWeather() {
        Awareness.getCaptureClient(this).getWeatherByDevice()
                .addOnSuccessListener(weatherStatusResponse -> {
                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    List<HourlyWeather> hourlyWeather = weatherStatus.getHourlyWeather();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String weather_info_hours = "";
                    for (int i = 0; i < hourlyWeather.size(); i++) {

//                            weather_info_hours+= dateFormat.format(hourlyWeather.get(i).getDateTimeStamp()) +" - "+
//                                    (hourlyWeather.get(i).isDayNight() ? "Day" : "Night")+"\n"+
//                                    "Temperature: " + hourlyWeather.get(i).getTempC() + "°C / "+hourlyWeather.get(i).getTempF() +"°F \n"+
//                                    "Rain Probability: " + (hourlyWeather.get(i).getRainprobability()<50?"Low":(hourlyWeather.get(i).getRainprobability()<75?"Medium":"High"))+"\n"+
//                                    "Rain Probability: " + hourlyWeather.get(i).getRainprobability()+"\n"+
//                                    "Weather Id: " +hourlyWeather.get(i).getWeatherId()+"\n\n";

                        //xửa lí adapter24
                        String time = "Time: " + dateFormat.format(hourlyWeather.get(i).getDateTimeStamp());
                        String dayNight = hourlyWeather.get(i).isDayNight() ? "Day" : "Night";
                        String temp = "Nhiệt độ: " + hourlyWeather.get(i).getTempC() + " °C";
                        String rain = "Tỷ lệ mưa: " + hourlyWeather.get(i).getRainprobability() + " %";
                        int urlImage;
                        if (hourlyWeather.get(i).isDayNight()) {
                            urlImage = R.raw.day;
                        } else {
                            urlImage = R.raw.night;
                        }

                        list_24h.add(new Weather24(time, dayNight, temp, rain, urlImage));

                    }
                    Adapterweather24.notifyDataSetChanged();

                    Log.i("hour", weather_info_hours);

                })
                .addOnFailureListener(e -> Log.e(TAG, "get Hourly weather failed"));
    }

    @SuppressLint("MissingPermission")
    private void getDailyWeather() {
        Awareness.getCaptureClient(this).getWeatherByDevice()
                .addOnSuccessListener(weatherStatusResponse -> {

                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    List<DailyWeather> dailyWeather = weatherStatus.getDailyWeather();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy ", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss ", Locale.getDefault());
                    StringBuilder daily_info = new StringBuilder();
                    for (int i = 0; i < dailyWeather.size(); i++) {
                        //FIXME string concat in loop
                        daily_info
                                .append("Date: ")
                                .append(dateFormat.format(dailyWeather.get(i).getDateTimeStamp()))
                                .append("\n")
                                .append("Sun Rise: ")
                                .append(dateFormat.format(dailyWeather.get(i).getSunRise()))
                                .append("\n")
                                .append("Sun Set: ")
                                .append(dateFormat.format(dailyWeather.get(i).getSunSet()))
                                .append("\n")
                                .append("Moon Set: ")
                                .append(dateFormat.format(dailyWeather.get(i).getMoonSet()))
                                .append("\n").append("Moon Rise: ")
                                .append(dateFormat.format(dailyWeather.get(i).getMoonRise()))
                                .append("\n").append("Moon Phase: ")
                                .append(dailyWeather.get(i).getMoonphase())
                                .append("\n")
                                .append("Aqi Value: ")
                                .append(dailyWeather.get(i).getAqiValue())
                                .append("\n")
                                .append("Temperature Max: ")
                                .append(dailyWeather.get(i).getMaxTempC())
                                .append("°C / ")
                                .append(dailyWeather.get(i).getMaxTempF())
                                .append("°F \n")
                                .append("Temperature Min: ")
                                .append(dailyWeather.get(i).getMinTempC())
                                .append("°C / ").append(dailyWeather.get(i).getMinTempF())
                                .append("°F \n").append("Day Weather Id: ")
                                .append(dailyWeather.get(i).getSituationDay().getWeatherId())
                                .append("\n").append("Night Weather Id: ")
                                .append(dailyWeather.get(i).getSituationNight().getWeatherId())
                                .append("\n").append("Day Wind Direction: ")
                                .append(dailyWeather.get(i).getSituationDay().getWindDir())
                                .append("\n").append("Night Wind Direction: ")
                                .append(dailyWeather.get(i).getSituationNight().getWindDir())
                                .append("\n").append("Day Wind Level: ")
                                .append(dailyWeather.get(i).getSituationDay().getWindLevel())
                                .append("\n").append("Night Wind Level: ")
                                .append(dailyWeather.get(i).getSituationNight().getWindLevel())
                                .append("\n").append("Day Wind Speed: ")
                                .append(dailyWeather.get(i).getSituationDay().getWindSpeed())
                                .append("\n").append("Night Wind Speed: ")
                                .append(dailyWeather.get(i).getSituationNight().getWindSpeed())
                                .append("\n\n");
//                        xửa lí adapter7
                        String date = "Date: " + dateFormat.format(dailyWeather.get(i).getDateTimeStamp());

                        String min = "Min: " + dailyWeather.get(i).getMinTempC() + " °C";
                        String max = "Max: " + dailyWeather.get(i).getMaxTempC() + " °C";
                        String sunset = "Sun set: " + timeFormat.format(dailyWeather.get(i).getSunSet());
                        String sunrise = "Sun rise: " + timeFormat.format(dailyWeather.get(i).getSunRise());

                        int id = getIdWeather(dailyWeather.get(i).getSituationDay().getWeatherId());
                        setImageWeather7days(id);

Log.d(TAG,dailyWeather.get(i).getMinTempC() + " °C");
                        list_7days.add(new Weather7(date, troi, min, max, sunset, sunrise, imageUrl));

                    }
                    adapterWeather7.notifyDataSetChanged();
                    Log.i("day", daily_info.toString());
                })
                .addOnFailureListener(e -> Log.e(TAG, "get day weather failed"));
    }

    private void set24hourWeather() {

        list_24h = new ArrayList<Weather24>();

        Adapterweather24 = new Adapterweather24(HomeActivity.this, android.R.layout.simple_list_item_1, list_24h);
        listview_24h.setAdapter(Adapterweather24);
    }

    private void set7daysWeather() {

        list_7days = new ArrayList<Weather7>();

        adapterWeather7 = new AdapterWeather7(HomeActivity.this, android.R.layout.simple_list_item_1, list_7days);
        listview_7day.setAdapter(adapterWeather7);
    }

    private void setImageWeather7days(int i) {
        // variable: Một biến để kiểm tra.
        switch (i) {
            case 1:
                // nắng ...
                troi = "Trời nắng";
                imageUrl = R.drawable.sun_50px_color;
                break;
            case 2:
                // mây ngắt quãng, nắng mờ...
                troi = "Có mây";
                imageUrl = R.drawable.partly_cloudy_day_50px_color;
                break;
            case 3:
                // nhieu mây ...
                troi = "Nhiều mây";
                imageUrl = R.drawable.cloud_50px_color;
                break;
            case 4:
                // sương ...
                troi = "Sương";
                imageUrl = R.drawable.dust_50px;
                break;
            case 5:
                // mưa ...
                troi = "Có mưa";
                imageUrl = R.drawable.rain_50px_color;
                break;
            case 6:
                // dông ...
                troi = "Có dông";
                imageUrl = R.drawable.storm_50px_color;
                break;
            case 7:
                // có tuyết ...
                troi = "Có tuyết";
                imageUrl = R.drawable.snow_50px_color;
                break;
            case 8:
                // đóng băng ...
                troi = "Băng";
                imageUrl = R.drawable.icy_50px;
                break;
            case 9:
                // mưa và tuyết ...
                textViewweatherid.setText("Có mưa tuyết");
                imageViewwtid.setImageResource(R.drawable.sleet_50px_color);
                break;
            default:
                // Làm gì đó tại đây ...
        }
    }

    private void dayNightBackground() {
        //backgrounf ngayf / ddeem
        Random rn = new Random();
        int answer = rn.nextInt(5) + 1;
//        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
//        textClock.getFormat12Hour();
        Log.i("clock1", answer + "\n");
//        if(Integer.parseInt(currentTime) > 17){
//            imageView_background.setImageResource(R.drawable.bg_night);
//            textView_7.setBackgroundResource(R.color.bg_text_night);
//            textView_24.setBackgroundResource(R.color.bg_text_night);
//
//        }else {
//            imageView_background.setImageResource(R.drawable.bg_morning);
//            textView_7.setBackgroundResource(R.color.bg_text_morning);
//            textView_24.setBackgroundResource(R.color.bg_text_morning);
//        }
        switch (answer) {
            case 1:
                // nắng ...
                imageView_background.setImageResource(R.drawable.bg_morning);
                textView_7.setBackgroundResource(R.color.bg_text_morning);
                textView_24.setBackgroundResource(R.color.bg_text_morning);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_morning)); //status bar or the time bar at the top
                break;
            case 2:
                imageView_background.setImageResource(R.drawable.bg_night);
                textView_7.setBackgroundResource(R.color.bg_text_night);
                textView_24.setBackgroundResource(R.color.bg_text_night);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_night)); //status bar or the time bar at the top
                break;
            case 3:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_weather);
                textView_7.setBackgroundResource(R.color.bg_text_night_blue);
                textView_24.setBackgroundResource(R.color.bg_text_night_blue);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_blue)); //status bar or the time bar at the top
                break;
            case 4:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_green_flower);
                textView_7.setBackgroundResource(R.color.bg_text_green);
                textView_24.setBackgroundResource(R.color.bg_text_green);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_green)); //status bar or the time bar at the top
                break;
            case 5:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_puper_flower);
                textView_7.setBackgroundResource(R.color.bg_text_puple);
                textView_24.setBackgroundResource(R.color.bg_text_puple);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_puple)); //status bar or the time bar at the top
                break;
            default:
                // Làm gì đó tại đây ...
        }

    }

    private boolean checkDayNight() {
        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        textClock.getFormat12Hour();
        Log.i("clock1", currentTime + "\n");
        return Integer.parseInt(currentTime) < 18;
    }


// set statusbar

    public void displayNotification(String text,String city,String weather) {

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        }

        //convert text to bitmap
        Bitmap bitmap = createBitmapFromString(text.trim());

        //setting bitmap to staus bar icon.
        builder.setSmallIcon(Icon.createWithBitmap(bitmap));

        builder.setContentTitle(city);
        builder.setContentText(weather);
        builder.setPriority(Notification.PRIORITY_MAX);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

        createNotificationChannel();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thông báo thời tiết";
            String description = " notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
    private Bitmap createBitmapFromString(String inputNumber) {

//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setTextSize(50);
//        paint.setTextAlign(Paint.Align.CENTER);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));

        Rect textBounds = new Rect();
        textPaint.getTextBounds(inputNumber, 0, inputNumber.length(), textBounds);

        Bitmap bitmap = Bitmap.createBitmap(textBounds.width() + 15, 90,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(inputNumber, textBounds.width() / 2 + 10, 70, textPaint);
        return bitmap;
    }

// push kit
    private void pushkit(){
        HmsMessaging.getInstance(HomeActivity.this).subscribe("vqh_alias").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Boolean isSuccessful = task.isSuccessful();
            }
        });
    }

    public class MyReceiver  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MyReceiver.class.getSimpleName(), "Air Plane mode");
        }
    }


}