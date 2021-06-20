package com.example.vqhcovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vqhcovid.News.News;
import com.example.vqhcovid.News.NewsAdapter;
import com.example.vqhcovid.weather.AdapterWeather7;
import com.example.vqhcovid.weather.Adapterweather24;
import com.example.vqhcovid.weather.Weather24;
import com.example.vqhcovid.weather.Weather7;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.capture.WeatherStatusResponse;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.DailyWeather;
import com.huawei.hms.kit.awareness.status.weather.HourlyWeather;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "weather";
    TextView textViewtemp, textViewweatherid, textViewcity, textViewwind, textViewhum, textViewrealtemp,textViewuv,
            textView_7,textView_24;
    ImageView imageViewwtid,imageView_background;
    TextClock textClock;


    ListView listview_24h;
    Adapterweather24 Adapterweather24;
    ArrayList<Weather24> list_24h;

    ListView listview_7day;
    AdapterWeather7 adapterWeather7;
    ArrayList<Weather7> list_7days;
    String troi="";
    int imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        Toast.makeText(WeatherActivity.this,"Đang tải dữ liệu . . .",Toast.LENGTH_LONG).show();
        //full màn
        // full màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

//        //clock
//        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
//        customAnalogClock.setAutoUpdate(true);
//        customAnalogClock.init(WeatherActivity.this, R.drawable.default_face, R.drawable.default_hour_hand, R.drawable.default_minute_hand, 0, false, false);

        anhxa();

        GPScheck();

        changeGPS();
//backgrounf ngayf / ddeem
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        textClock.getFormat12Hour();
        Log.i("clock1",currentTime+"\n" );
        if(Integer.parseInt(currentTime) > 17){
            imageView_background.setImageResource(R.drawable.bg_night);
            textView_7.setBackgroundResource(R.color.bg_text_night);
            textView_24.setBackgroundResource(R.color.bg_text_night);

        }else {
            imageView_background.setImageResource(R.drawable.bg_morning);
            textView_7.setBackgroundResource(R.color.bg_text_morning);
            textView_24.setBackgroundResource(R.color.bg_text_morning);
        }
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

    private void anhxa(){
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
    }

    //check quyen vi tri
    private void GPScheck(){
        int permission_internet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int permission_lc1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_lc2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission_internet != PackageManager.PERMISSION_GRANTED
                || permission_lc1 != PackageManager.PERMISSION_GRANTED
                || permission_lc2 != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }

    // bắt buộc bật vị trí
    private void changeGPS(){
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                // khoi dong lại intent
                resetintent();

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
                WeatherActivity.this.recreate();
            }
        },3000);

    }
    //set hình ảnh:
    private  void Setimageweather(int i){
        // variable: Một biến để kiểm tra.
        switch ( i ) {
            case  1:
                // nắng ...
                textViewweatherid.setText("Trời nắng");
                imageViewwtid.setImageResource(R.drawable.sun_50px_color);
                break;
            case  2:
                // mây ngắt quãng, nắng mờ...
                textViewweatherid.setText("Có mây");
                imageViewwtid.setImageResource(R.drawable.partly_cloudy_day_50px_color);
                break;
            case  3:
                // nhieu mây ...
                textViewweatherid.setText("Nhiều mây");
                imageViewwtid.setImageResource(R.drawable.cloud_50px_color);
                break;
            case  4:
                // sương ...
                textViewweatherid.setText("Sương");
                imageViewwtid.setImageResource(R.drawable.dust_50px);
                break;
            case  5:
                // mưa ...
                textViewweatherid.setText("Có mưa");
                imageViewwtid.setImageResource(R.drawable.rain_50px_color);
                break;
            case  6:
                // dông ...
                textViewweatherid.setText("Có dông");
                imageViewwtid.setImageResource(R.drawable.storm_50px_color);
                break;
            case  7:
                // có tuyết ...
                textViewweatherid.setText("Có tuyết");
                imageViewwtid.setImageResource(R.drawable.snow_50px_color);
                break;
            case  8:
                // đóng băng ...
                textViewweatherid.setText("Băng");
                imageViewwtid.setImageResource(R.drawable.icy_50px);
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
//                    String weatherInfoStr = "City:" + weatherSituation.getCity().getName() + "\n" +
//                            "Weather id is " + situation.getWeatherId() + "\n" +
//                            "CN Weather id is " + situation.getCnWeatherId() + "\n" +
//                            "Temperature is " + situation.getTemperatureC() + "℃" +
//                            "," + situation.getTemperatureF() + "℉" + "\n" +
//                            "Wind speed is " + situation.getWindSpeed() + "km/h" + "\n" +
//                            "Wind direction is " + situation.getWindDir() + "\n" +
//                            "Humidity is " + situation.getHumidity() + "%" +"\n"+
//                            "Readfeel is " +situation.getRealFeelC() + "\n"+
//                            "Uv is " +situation.getUvIndex() + "\n" +
//                            "Update is " +situation.getUpdateTime() + "\n";
//                    Log.i(TAG, weatherInfoStr);


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
                                urlimage = R.drawable.daysun_50px_color;
                            }else {
                                urlimage = R.drawable.moon_50px_color;
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

        Adapterweather24 = new Adapterweather24(WeatherActivity.this, android.R.layout.simple_list_item_1, list_24h);
        listview_24h.setAdapter(Adapterweather24);
    }
    private  void set7daysweather(){
        listview_7day = findViewById(R.id.listview_7days);
        list_7days = new ArrayList<Weather7>();

        adapterWeather7 = new AdapterWeather7(WeatherActivity.this, android.R.layout.simple_list_item_1, list_7days);
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
}