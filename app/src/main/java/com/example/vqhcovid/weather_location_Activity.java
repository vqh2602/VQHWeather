package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vqhcovid.City.City;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.capture.WeatherPosition;
import com.huawei.hms.kit.awareness.capture.WeatherStatusResponse;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class weather_location_Activity extends AppCompatActivity {

    private SearchService searchService;
    private TextView textViewweatherid,textView_nhietdo,textView_timezone,textView_nhietdo_f;
    private LottieAnimationView imageViewwtid;
    private EditText queryInput;
    private ListView listView_local;
    private LinearLayout layout_dof;
    private ArrayList<City> loca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_location);

        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode("CgB6e3x9pU7dsb3IvZA+LYt/XRBeEO3Hn6MM4sQnni5mYCGRzLt0XcCpX9JPuyi58n8F6BPwvzw1Gv3oVAS5kJQl", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("ThirdActivity", "encode apikey error");
        }

        queryInput = findViewById(R.id.editText_loca);
        textViewweatherid = findViewById(R.id.textViewweatherid);
        textView_nhietdo = findViewById(R.id.textView_nhietdo);
        textView_timezone = findViewById(R.id.textView_timezone);
        textView_nhietdo_f = findViewById(R.id.textView_nhietdo_f);
        imageViewwtid = findViewById(R.id.imageViewwtid);
        layout_dof = findViewById(R.id.layout_dof);
//        resultTextView = findViewById(R.id.textView2);
        listView_local = findViewById(R.id.listView_local);



        textViewweatherid.setVisibility(View.INVISIBLE);
        textView_nhietdo.setVisibility(View.INVISIBLE);
        textView_timezone.setVisibility(View.INVISIBLE);
        textView_nhietdo_f.setVisibility(View.INVISIBLE);
        imageViewwtid.setVisibility(View.INVISIBLE);
        layout_dof.setVisibility(View.INVISIBLE);
        listView_local.setVisibility(View.INVISIBLE);
        loca = new ArrayList<City>();

//        WeatherPosition position = new WeatherPosition();
//                position.setCity(loca.get(i).getCity());
//                position.setCountry(loca.get(i).getCountry());
////        position.setCity("London");
////        position.setCountry("United Kingdom");
//        position.setLocale("en_GB");
//        getweather(position);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar_local));

    }

    public void search(View view) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(queryInput.getWindowToken(), 0);
        Toast.makeText(weather_location_Activity.this, "Đang Tìm Kiếm... ", Toast.LENGTH_LONG).show();
        listView_local.setVisibility(View.VISIBLE);
        textViewweatherid.setVisibility(View.INVISIBLE);
        textView_nhietdo.setVisibility(View.INVISIBLE);
        textView_timezone.setVisibility(View.INVISIBLE);
        textView_nhietdo_f.setVisibility(View.INVISIBLE);
        imageViewwtid.setVisibility(View.INVISIBLE);
        layout_dof.setVisibility(View.INVISIBLE);
        loca.clear();

        TextSearchRequest textSearchRequest = new TextSearchRequest();
        textSearchRequest.setQuery(queryInput.getText().toString());
//        textSearchRequest.setHwPoiType(HwLocationType.TOWER);

        searchService.textSearch(textSearchRequest, new SearchResultListener<TextSearchResponse>() {
            @Override
            public void onSearchResult(TextSearchResponse textSearchResponse) {

                StringBuilder response = new StringBuilder("\n");
//                response.append("\n");
                int count = 0;
                AddressDetail addressDetail;
                if (null != textSearchResponse) {
                    if (null != textSearchResponse.getSites()) {
                        for (Site site : textSearchResponse.getSites()) {
                            addressDetail = site.getAddress();

//                            response
//                                    .append(String.format("[%s]  name: %s, formatAddress: %s, country: %s, countryCode: %s \r\n",
//                                            "" + (count++), site.getName(), site.getFormatAddress(),
//                                            (addressDetail == null ? "" : addressDetail.getCountry()),
//                                            (addressDetail == null ? "" : addressDetail.getCountryCode())));
                            Log.d("ThirdActivity", "search result is : " + site.getName() + "\n" + site.getFormatAddress()
                                    + "\n" + addressDetail.getCountry() +
                                    "\n" + addressDetail.getCountryCode());


//                            response1.append()
//                            loca.add("Địa Chỉ: " + site.getFormatAddress()
//                                    + "\nQuốc Gia: " + addressDetail.getCountry() +
//                                    "\nMã Quốc Gia: " + addressDetail.getCountryCode());
                            loca.add(new City(queryInput.getText().toString(),addressDetail.getCountry(),"en_GB",site.getFormatAddress() ));


                        }
                    } else {
                        response.append("textSearchResponse.getSites() is null!");
                    }
                } else {
                    response.append("textSearchResponse is null!");
                }
//                Log.d("ThirdActivity", "search result is : " + response);
//                resultTextView.setText(response.toString());
                setdl();
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
//                Log.e("ThirdActivitty", "onSearchError is: " + searchStatus.getErrorCode());
            }
        });


    }

    private void setdl() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, loca);
        // Bước 3: Gắn ArrayAdapter vào ListView
        listView_local.setAdapter(aa);

        aa.notifyDataSetChanged();

        // Bước 4: Xử lý sự kiện long click
        listView_local.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String data = String.valueOf(loca.get(i).getCity()+", "+loca.get(i).getCountry());

                WeatherPosition position = new WeatherPosition();
                position.setCity(loca.get(i).getCity());
                position.setCountry(loca.get(i).getCountry());
//                position.setCity("London");
//                position.setCountry("United Kingdom");
                position.setLocale("en_GB");
                getweather(position);

//
//                Log.e("ThirdActivitty", "onSearchError is2: " + position);
//
//
//
//                Log.e("ThirdActivitty", "onSearchError is1: " + data);


                Toast.makeText(weather_location_Activity.this, "Bạn đã chọn " + data, Toast.LENGTH_SHORT).show();
                listView_local.setVisibility(View.INVISIBLE);
                textViewweatherid.setVisibility(View.VISIBLE);
                textView_nhietdo.setVisibility(View.VISIBLE);
                textView_timezone.setVisibility(View.VISIBLE);
                textView_nhietdo_f.setVisibility(View.VISIBLE);
                imageViewwtid.setVisibility(View.VISIBLE);
                layout_dof.setVisibility(View.VISIBLE);







            }
        });


//        listView_local.setOnItemClickListener()(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                String data = (String) loca.get(i);
//                Log.e("ThirdActivitty", "onSearchError is: " +data);
//                Toast.makeText(weather_location_Activity.this,"Bạn đã chọn " + data, Toast.LENGTH_SHORT).show();
//                listView_local.setVisibility(View.INVISIBLE);
//                return false;
//            }
//        });
    }


    private void getweather(WeatherPosition position) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

        Log.e("localweather+ :/ ", String.valueOf(position));

            Awareness.getCaptureClient(this).getWeatherByPosition(position).addOnSuccessListener(weatherStatusResponse -> {
            WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
            WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
            Situation situation = weatherSituation.getSituation();

            if(situation != null){
                setImageWeather(getIdWeather(situation.getWeatherId()));

                String city = "City: " + weatherSituation.getCity().getName();
                String hum = "Humidity: " + situation.getHumidity() + " %";
                String wind = "Wind speed: " + situation.getWindSpeed() + " km/h";
                String tempc = situation.getTemperatureC() + "°";
                String tempf = situation.getTemperatureF() + "°F";
                String realtemp = "Cảm giác như: " + situation.getRealFeelC() + "°";
                textView_nhietdo.setText(tempc);
                textView_timezone.setText("time zone: " + weatherSituation.getCity().getTimeZone());
                textView_nhietdo_f.setText(tempf);
            }
            else {
                Toast.makeText(weather_location_Activity.this, "Thành phố bạn nhập có thể bị sai. hoặc chưa có dữ liệu thời tiết" , Toast.LENGTH_LONG).show();
            }
//                // Callback listener for execution success.
//                .addOnSuccessListener(weatherStatusResponse -> {
//                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
//                    WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
//                    Situation situation = weatherSituation.getSituation();

//            String weatherInfoStr = "City:" + weatherSituation.getCity().getName() + "\n" +
//                    "Weather id is " + situation.getWeatherId() + "\n" +
//                    "CN Weather id is " + situation.getCnWeatherId() + "\n" +
//                    "Temperature is " + situation.getTemperatureC() + "℃" +
//                    "," + situation.getTemperatureF() + "℉" + "\n" +
//                    "Wind speed is " + situation.getWindSpeed() + "km/h" + "\n" +
//                    "Wind direction is " + situation.getWindDir() + "\n" +
//                    "Humidity is " + situation.getHumidity() + "%" + "\n" +
//                    "Readfeel is " + situation.getRealFeelC() + "\n" +
//                    "Uv is " + situation.getUvIndex() + "\n" +
//                    "Update is " + situation.getUpdateTime() + "\n"
//                    + "time zone: " + weatherSituation.getCity().getTimeZone();
//            Log.e("localweather1", String.valueOf(weatherStatus));
//            Log.e("localweather2", String.valueOf(situation));
//            Log.e("localweather3", weatherInfoStr);



        });
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

        private void setImageWeather(int i) {
            // variable: Một biến để kiểm tra.
            switch (i) {
                case 1:
                    // nắng ...
                        textViewweatherid.setText("Trời nắng");
                        imageViewwtid.setAnimation(R.raw.sun);
                        imageViewwtid.playAnimation();
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
                        imageViewwtid.setAnimation(R.raw.rain);
                        imageViewwtid.playAnimation();
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

//        private boolean checkDayNight() {
//            String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
//            textClock.getFormat12Hour();
//            Log.i("clock1", currentTime + "\n");
//            return Integer.parseInt(currentTime) < 18;
//        }

}