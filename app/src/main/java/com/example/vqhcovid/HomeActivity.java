package com.example.vqhcovid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vqhcovid.weather.AdapterWeather7;
import com.example.vqhcovid.weather.Adapterweather24;
import com.example.vqhcovid.weather.Weather24;
import com.example.vqhcovid.weather.Weather7;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.capture.WeatherStatusResponse;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.DailyWeather;
import com.huawei.hms.kit.awareness.status.weather.HourlyWeather;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.example.vqhcovid.R.drawable.ic_toolbar_menu_white;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;



    String displayname_check ="",skip ="",fullname,disname,imgurl;



    private static final String TAG = "weather";
    TextView textViewtemp, textViewweatherid, textViewcity, textViewwind, textViewhum, textViewrealtemp,textViewuv,
            textView_7,textView_24;
    ImageView imageView_background;
    TextClock textClock;
    LottieAnimationView imageViewwtid;

    ListView listview_24h;
    com.example.vqhcovid.weather.Adapterweather24 Adapterweather24;
    ArrayList<Weather24> list_24h;

    ListView listview_7day;
    AdapterWeather7 adapterWeather7;
    ArrayList<Weather7> list_7days;
    String troi="";
    int imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        anhxa();
        actionToolBar();

        // check ddang nhap
        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
        AccountAuthService service = AccountAuthManager.getService(HomeActivity.this, authParams);
        Task<AuthAccount> task = service.silentSignIn();

        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                // Obtain the user's ID information.
                Log.i(TAG, "displayName:" + authAccount.getDisplayName());
                // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
                Log.i(TAG, "accountFlag:" + authAccount.getAccountFlag());
                dulieuinten(authAccount);

        fullname = authAccount.getFamilyName ()+" " + authAccount.getGivenName();
        imgurl = authAccount.getAvatarUriString();
        disname =  authAccount.getDisplayName();
        displayname_check="ok";
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // The sign-in failed. Try to sign in explicitly using getSignInIntent().
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.i(TAG, "sign failed status:" + apiException.getStatusCode());
                    Toast.makeText(HomeActivity.this,"Ban chua dang nhap",Toast.LENGTH_LONG).show();

                    displayname_check = "";
                    Intent intent = getIntent();
                    skip =intent.getStringExtra("skip");
                    Log.i(TAG, "displayNameskip:" + skip);
                    if(skip == null) {
                        Intent intent1 = new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(intent1);
                        finish();
                    }

                }
            }
        });




        //khi click vo list se doi mau
        navigationView.bringToFront();
        // hieejn tool bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toolbar, R.string.close_toolbar);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
        //đặt sau syncstate mới hd.-> set icon toolbar moi co thể thay đôi đc icon và màu
        toolbar.setNavigationIcon(ic_toolbar_menu_white);


        //khai báo sự kiện onclick từng item menu ->public boolean onNavigationItemSelected
        navigationView.setNavigationItemSelectedListener(this);




        daynightbackground();



        Toast.makeText(HomeActivity.this,"Đang tải dữ liệu . . .",Toast.LENGTH_LONG).show();
//        //full màn
//        // full màn hình
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

//        //clock
//        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
//        customAnalogClock.setAutoUpdate(true);
//        customAnalogClock.init(WeatherActivity.this, R.drawable.default_face, R.drawable.default_hour_hand, R.drawable.default_minute_hand, 0, false, false);

        anhxa();

        GPScheck();

        changeGPS();
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // xửa lí adapter
        set24hourweather();
        set7daysweather();

        getnowweather();
        getHourlyWeather();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDailyWeather();
            }
        },5000);

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

    // khoi tao thu vien ho tro toolbar va  onclick
    private void actionToolBar() {
        // taoj thu vien ho tro
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //set icon
        toolbar.setNavigationIcon(ic_toolbar_menu_white);
        //toolbar.getNavigationIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

        //bat su kien onclick
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    private void anhxa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        textViewtemp = findViewById(R.id.textViewtemp);
        textViewweatherid =findViewById(R.id.textViewweatherid);
        textViewcity =findViewById(R.id.textViewcity);
        textViewwind =findViewById(R.id.textViewwind);
        textViewhum =findViewById(R.id.textViewhum);
        imageViewwtid =findViewById(R.id.imageViewwtid);
        textViewrealtemp = findViewById(R.id.textViewrealtemp);
        textViewuv = findViewById(R.id.textViewuv);
        textClock = findViewById(R.id.textclock);
        imageView_background = findViewById(R.id.imageView_background);
        textView_7 = findViewById(R.id.textView_7);
        textView_24 = findViewById(R.id.textView_24);
//        listView = (ListView) findViewById(R.id.listview_menu);
    }

    // hàm sử lí sự kiện khi click vào menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home_main) {
            //check
            //check
            if(displayname_check != null){
                dlaccount();
            }
            else {
                Toast.makeText(HomeActivity.this,"Bạn Chưa Đăng Nhập",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        } else if (id == R.id.tintuc_main) {
            //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(HomeActivity.this, InformationRssActivity.class);
            Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
            startActivity(intent);

        }else if (id == R.id.QR_main) {
                //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, QRscanActivity.class);
                startActivity(intent);

        }else if (id == R.id.About_main) {
            //Toast.makeText(HomeActivity.this,"mask click",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);

        }else if (id == R.id.sp_ins) {
            Toast.makeText(HomeActivity.this, "instagram", Toast.LENGTH_LONG).show();
            GoToURL("https://www.instagram.com/vqh.26/");
        } else if (id == R.id.sp_fb) {
            Toast.makeText(HomeActivity.this, "facebook", Toast.LENGTH_LONG).show();
            GoToURL("https://www.facebook.com/");
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
    private void GoToURL(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //check du lieu intent
    private void dulieuinten(AuthAccount authAccount){
//        Intent intent = getIntent();
//        String displayname = intent.getStringExtra("Displayname");
//        fullname = intent.getStringExtra("fullname");
//        imgurl = intent.getStringExtra("Imangeurl");
//        disname =  intent.getStringExtra("Displayname");

            //check
            if(authAccount.getDisplayName() != null){
                Toast.makeText(HomeActivity.this,"Xin Chào: "+authAccount.getDisplayName(),Toast.LENGTH_LONG).show();

            }
            else {
              Toast.makeText(HomeActivity.this,"Bạn chưa đăng nhập ",Toast.LENGTH_LONG).show();
            }



    }
    //truyendulieu account
    private void dlaccount(){
        Intent intent = new Intent(HomeActivity.this,AccountActivity.class);
        intent.putExtra("Displayname", disname);
        intent.putExtra("fullname", fullname);
        intent.putExtra("Imangeurl", imgurl);
        startActivity(intent);
    }

//check quyen vi tri
    private void GPScheck(){
        int permission_internet = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);
        int permission_lc1= ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_lc2 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission_internet != PackageManager.PERMISSION_GRANTED
                || permission_lc1 != PackageManager.PERMISSION_GRANTED
                || permission_lc2 != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }

    // bắt buộc bật vị trí
    private void changeGPS(){
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                // khoi dong lại intent
                //resetintent();

            }
        }
        private void buildAlertMessageNoGps() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

    }

    // reset intent
    private void resetintent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.this.recreate();
            }
        },3000);

    }
    //set hình ảnh:
    private  void Setimageweather(int i){
        // variable: Một biến để kiểm tra.
        switch ( i ) {
            case  1:
                // nắng ...

                if(checkdaynight()){
                    textViewweatherid.setText("Trời nắng");
                    imageViewwtid.setAnimation(R.raw.sun);
                    imageViewwtid.playAnimation();
                }
                else {
                    textViewweatherid.setText("Trời trong");
                    imageViewwtid.setAnimation(R.raw.wnight);
                    imageViewwtid.playAnimation();
                }


                break;
            case  2:
                // mây ngắt quãng, nắng mờ...
                textViewweatherid.setText("Có mây");
//                imageViewwtid.setImageResource(R.drawable.partly_cloudy_day_50px_color);
                    imageViewwtid.setAnimation(R.raw.partlycould);
                    imageViewwtid.playAnimation();

                break;
            case  3:
                // nhieu mây ...
                textViewweatherid.setText("Nhiều mây");
//                imageViewwtid.setImageResource(R.drawable.cloud_50px_color);
                imageViewwtid.setAnimation(R.raw.clould);
                imageViewwtid.playAnimation();
                break;
            case  4:
                // sương ...
                textViewweatherid.setText("Sương");
//                imageViewwtid.setImageResource(R.drawable.dust_50px);
                imageViewwtid.setAnimation(R.raw.dust);
                imageViewwtid.playAnimation();
                break;
            case  5:
                // mưa ...
                textViewweatherid.setText("Có mưa");
//                imageViewwtid.setImageResource(R.drawable.rain_50px_color);
                if(checkdaynight()){
                    imageViewwtid.setAnimation(R.raw.rain);
                    imageViewwtid.playAnimation();
                }
                else {
                    imageViewwtid.setAnimation(R.raw.rainynight);
                    imageViewwtid.playAnimation();
                }

                break;
            case  6:
                // dông ...
                textViewweatherid.setText("Có dông");
//                imageViewwtid.setImageResource(R.drawable.storm_50px_color);
                imageViewwtid.setAnimation(R.raw.strom);
                imageViewwtid.playAnimation();
                break;
            case  7:
                // có tuyết ...
                textViewweatherid.setText("Có tuyết");
//                imageViewwtid.setImageResource(R.drawable.snow_50px_color);
                imageViewwtid.setAnimation(R.raw.snow);
                imageViewwtid.playAnimation();
                break;
            case  8:
                // đóng băng ...
                textViewweatherid.setText("Băng");
//                imageViewwtid.setImageResource(R.drawable.icy_50px);
                imageViewwtid.setAnimation(R.raw.icy);
                imageViewwtid.playAnimation();
                break;
            case  9:
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
    //getidweather
    private int getidweather(int i){
        int id = 0;
        if(i ==1 || i==2 ||i==3 || i==30 || i==33 ){
            id = 1;
        } else  if(i ==4 || i==5 ||i==6 || i==34 || i==37){
            id = 2;
        } else if(i ==7 || i==35 ||i==36 || i==38 ){
            id=3;
        } else if(i==11 ||i==43 || i==44 ){
            id=4;
        }else if(i ==12 || i==13 ||i==14 ||  i==39 || i==40 ||i ==8 ){
            id=5;
        } else if(i ==15 || i==16 ||i==17 || i==18 || i==41 || i==42 ) {
            id = 6;
        }else if(i ==19 || i==20 ||i==21 || i==22 || i==23 || i==31 ) {
            id = 7;
        }else if(i ==24 || i==25 ||i==26  ) {
            id = 8;
        }else if(i ==29 ) {
            id = 9;
        }

        return id;
    }

    //set uv
    private  void Setuv(int i){
        // variable: Một biến để kiểm tra.
        switch ( i ) {
            case  0:
                // không có ...
                textViewuv.setText("UV: không có");
                break;
            case  1:
                // tia UV rất yếu.
                textViewuv.setText("UV: rất yếu");
                break;
            case  2:
                // UV yếu
                textViewuv.setText("UV: yếu");
                break;
            case  3:
                //  tia trung bình
                textViewuv.setText("UV: trung bình");
                break;
            case  4:
                // mạnh
                textViewuv.setText("UV: mạnh");
                break;
            case  5:
                // rất mạnh
                textViewuv.setText("UV: rất mạnh");
                break;
            default:
                // Làm gì đó tại đây ...
        }
    }

@SuppressLint("MissingPermission")
private void getnowweather(){
    // lấy thời tiết hiện tại
    Awareness.getCaptureClient(this).getWeatherByDevice()
            // Callback listener for execution success.
            .addOnSuccessListener(new OnSuccessListener<WeatherStatusResponse>() {

                @Override
                public void onSuccess(WeatherStatusResponse weatherStatusResponse) {
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
                            "Humidity is " + situation.getHumidity() + "%" +"\n"+
                            "Readfeel is " +situation.getRealFeelC() + "\n"+
                            "Uv is " +situation.getUvIndex() + "\n" +
                            "Update is " +situation.getUpdateTime() + "\n";
                    Log.i(TAG, weatherInfoStr);


                    // xuwr lis dl
                    Setimageweather(getidweather(situation.getWeatherId()));
                    Setuv(situation.getUvIndex());
                    String city = "City: " + weatherSituation.getCity().getName();
                    String hum = "Humidity: " + situation.getHumidity() + " %";
                    String wind = "Wind speed: " + situation.getWindSpeed() + " km/h";
                    String temp = situation.getTemperatureC() + "°";
                    String realtemp = "Cảm giác như: "+situation.getRealFeelC()  + "°";
                    textViewcity.setText(city);
                    textViewhum.setText(hum);
                    textViewwind.setText(wind);
                    textViewtemp.setText(temp);
                    textViewrealtemp.setText(realtemp);
                }
            })
            // Callback listener for execution failure.
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "get weather failed");
                }
            });
}

//    thời tiết theo giờ
    @SuppressLint("MissingPermission")
    private void getHourlyWeather() {
        Awareness.getCaptureClient(this).getWeatherByDevice()
                .addOnSuccessListener(new OnSuccessListener<WeatherStatusResponse>() {
                    @Override
                    public void onSuccess(WeatherStatusResponse weatherStatusResponse) {
                        WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                        List<HourlyWeather> hourlyWeather = weatherStatus.getHourlyWeather();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String weather_info_hours="";
                        for(int i=0;i<=24;i++) {

//                            weather_info_hours+= dateFormat.format(hourlyWeather.get(i).getDateTimeStamp()) +" - "+
//                                    (hourlyWeather.get(i).isDayNight() ? "Day" : "Night")+"\n"+
//                                    "Temperature: " + hourlyWeather.get(i).getTempC() + "°C / "+hourlyWeather.get(i).getTempF() +"°F \n"+
//                                    "Rain Probability: " + (hourlyWeather.get(i).getRainprobability()<50?"Low":(hourlyWeather.get(i).getRainprobability()<75?"Medium":"High"))+"\n"+
//                                    "Rain Probability: " + hourlyWeather.get(i).getRainprobability()+"\n"+
//                                    "Weather Id: " +hourlyWeather.get(i).getWeatherId()+"\n\n";

                            //xửa lí adapter24
                            String time = "Time: "+ dateFormat.format(hourlyWeather.get(i).getDateTimeStamp());
                            String day_night =  hourlyWeather.get(i).isDayNight() ? "Day" : "Night";
                            String temp ="Nhiệt độ: "+  hourlyWeather.get(i).getTempC()+ " °C";
                            String rain ="Tỷ lệ mưa: "+  hourlyWeather.get(i).getRainprobability() +" %";
                            int urlimage;
                            if(hourlyWeather.get(i).isDayNight()){
                                urlimage = R.raw.day;
                            }else {
                                urlimage = R.raw.night;
                            }

                            list_24h.add(new Weather24(time,day_night,temp,rain,urlimage));

                        }
                        Adapterweather24.notifyDataSetChanged();

                        Log.i("hour",weather_info_hours);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "get Hourly weather failed");
                    }
                });
    }

//    thời tiết thep ngay
@SuppressLint("MissingPermission")
private void getDailyWeather(){
    Awareness.getCaptureClient(this).getWeatherByDevice()
            .addOnSuccessListener(new OnSuccessListener<WeatherStatusResponse>() {
                @Override
                public void onSuccess(WeatherStatusResponse weatherStatusResponse) {

                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    List<DailyWeather> dailyWeather = weatherStatus.getDailyWeather();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy ");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss ");
                    String daily_info="";
                    for(int i=0;i<7;i++) {
                        daily_info += "Date: " + dateFormat.format(dailyWeather.get(i).getDateTimeStamp()) + "\n" +
                                "Sun Rise: " + dateFormat.format(dailyWeather.get(i).getSunRise()) + "\n" +
                                "Sun Set: " + dateFormat.format(dailyWeather.get(i).getSunSet()) + "\n" +
                                "Moon Set: " + dateFormat.format(dailyWeather.get(i).getMoonSet()) + "\n" +
                                "Moon Rise: " + dateFormat.format(dailyWeather.get(i).getMoonRise()) + "\n" +
                                "Moon Phase: " + dailyWeather.get(i).getMoonphase() + "\n" +
                                "Aqi Value: " + dailyWeather.get(i).getAqiValue() + "\n" +
                                "Temperature Max: " + dailyWeather.get(i).getMaxTempC() + "°C / " + dailyWeather.get(i).getMaxTempF() + "°F \n" +
                                "Temperature Min: " + dailyWeather.get(i).getMinTempC() + "°C / " + dailyWeather.get(i).getMinTempF() + "°F \n" +
                                "Day Weather Id: " + dailyWeather.get(i).getSituationDay().getWeatherId() + "\n" +
                                "Night Weather Id: " + dailyWeather.get(i).getSituationNight().getWeatherId() + "\n" +
                                "Day Wind Direction: " + dailyWeather.get(i).getSituationDay().getWindDir() + "\n" +
                                "Night Wind Direction: " + dailyWeather.get(i).getSituationNight().getWindDir() + "\n" +
                                "Day Wind Level: " + dailyWeather.get(i).getSituationDay().getWindLevel() + "\n" +
                                "Night Wind Level: " + dailyWeather.get(i).getSituationNight().getWindLevel() + "\n" +
                                "Day Wind Speed: " + dailyWeather.get(i).getSituationDay().getWindSpeed() + "\n" +
                                "Night Wind Speed: " + dailyWeather.get(i).getSituationNight().getWindSpeed() + "\n\n";
                        //xửa lí adapter7
                        String date = "Date: "+ dateFormat.format(dailyWeather.get(i).getDateTimeStamp());

                        String min ="Min: "+  dailyWeather.get(i).getMinTempC()+ " °C";
                        String max ="Max: "+  dailyWeather.get(i).getMaxTempC() +" °C";
                        String sunset ="Sun set: "+  timeFormat.format(dailyWeather.get(i).getSunSet());
                        String sunrise ="Sun rise: "+  timeFormat.format(dailyWeather.get(i).getSunRise());

                        int id =getidweather(dailyWeather.get(i).getSituationDay().getWeatherId()) ;
                        Setimageweather7days(id);


                        list_7days.add(new Weather7(date,troi,min,max,sunset,sunrise,imageUrl));

                    }
                    adapterWeather7.notifyDataSetChanged();
                    Log.i("day",daily_info);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "get day weather failed");

                }
            });
}

//set adapter 24h
    private  void set24hourweather(){
        listview_24h = findViewById(R.id.listview_24h);
        list_24h = new ArrayList<Weather24>();

        Adapterweather24 = new Adapterweather24(HomeActivity.this, android.R.layout.simple_list_item_1, list_24h);
        listview_24h.setAdapter(Adapterweather24);
    }
    private  void set7daysweather(){
        listview_7day = findViewById(R.id.listview_7days);
        list_7days = new ArrayList<Weather7>();

        adapterWeather7 = new AdapterWeather7(HomeActivity.this, android.R.layout.simple_list_item_1, list_7days);
        listview_7day.setAdapter(adapterWeather7);
    }

    //setimage 7days
    private  void Setimageweather7days(int i){
        // variable: Một biến để kiểm tra.
        switch ( i ) {
            case  1:
                // nắng ...
                troi = "Trời nắng";
                imageUrl = R.drawable.sun_50px_color;
                break;
            case  2:
                // mây ngắt quãng, nắng mờ...
                troi = "Có mây";
                imageUrl = R.drawable.partly_cloudy_day_50px_color;
                break;
            case  3:
                // nhieu mây ...
                troi = "Nhiều mây";
                imageUrl = R.drawable.cloud_50px_color;
                break;
            case  4:
                // sương ...
                troi = "Sương";
                imageUrl = R.drawable.dust_50px;
                break;
            case  5:
                // mưa ...
                troi = "Có mưa";
                imageUrl = R.drawable.rain_50px_color;
                break;
            case  6:
                // dông ...
                troi = "Có dông";
                imageUrl = R.drawable.storm_50px_color;
                break;
            case  7:
                // có tuyết ...
                troi = "Có tuyết";
                imageUrl = R.drawable.snow_50px_color;
                break;
            case  8:
                // đóng băng ...
                troi = "Băng";
                imageUrl =R.drawable.icy_50px;
                break;
            case  9:
                // mưa và tuyết ...
                textViewweatherid.setText("Có mưa tuyết");
                imageViewwtid.setImageResource(R.drawable.sleet_50px_color);
                break;
            default:
                // Làm gì đó tại đây ...
        }
    }

    private void daynightbackground(){
        //backgrounf ngayf / ddeem
        Random rn = new Random();
        int answer = rn.nextInt(5) + 1;
//        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
//        textClock.getFormat12Hour();
        Log.i("clock1",answer+"\n" );
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
        switch ( answer ) {
            case  1:
                // nắng ...
                imageView_background.setImageResource(R.drawable.bg_morning);
                textView_7.setBackgroundResource(R.color.bg_text_morning);
                textView_24.setBackgroundResource(R.color.bg_text_morning);
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_nav)); // Navigation bar the soft bottom
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_morning)); //status bar or the time bar at the top
                }
                break;
            case  2:
                imageView_background.setImageResource(R.drawable.bg_night);
                textView_7.setBackgroundResource(R.color.bg_text_night);
                textView_24.setBackgroundResource(R.color.bg_text_night);
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_nav)); // Navigation bar the soft bottom
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_night)); //status bar or the time bar at the top
                }
                break;
            case  3:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_weather);
                textView_7.setBackgroundResource(R.color.bg_text_night_blue);
                textView_24.setBackgroundResource(R.color.bg_text_night_blue);
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_nav)); // Navigation bar the soft bottom
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_blue)); //status bar or the time bar at the top
                }
                break;
            case  4:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_green_flower);
                textView_7.setBackgroundResource(R.color.bg_text_green);
                textView_24.setBackgroundResource(R.color.bg_text_green);
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_nav)); // Navigation bar the soft bottom
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_green)); //status bar or the time bar at the top
                }
                break;
            case  5:
                // nhieu mây ...
                imageView_background.setImageResource(R.drawable.bg_puper_flower);
                textView_7.setBackgroundResource(R.color.bg_text_puple);
                textView_24.setBackgroundResource(R.color.bg_text_puple);
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_nav)); // Navigation bar the soft bottom
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_puple)); //status bar or the time bar at the top
                }
                break;
            default:
                // Làm gì đó tại đây ...
        }

    }

    // kiểm tra ngày hay đêm
    private boolean checkdaynight(){
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        textClock.getFormat12Hour();
        Log.i("clock1",currentTime+"\n" );
        if(Integer.parseInt(currentTime) < 18){
            return true;

        }else {
            return  false;
        }
    }
}