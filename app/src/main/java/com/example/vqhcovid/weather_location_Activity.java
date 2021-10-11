package com.example.vqhcovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;


public class weather_location_Activity extends AppCompatActivity {

    private SearchService searchService;
    private TextView resultTextView;
    private EditText queryInput;
    private ListView listView_local;
    private ArrayList<City> loca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_location);

        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode("CwEAAAAAgwvt+2kdW6emEReI89tKa5I8ZoYKglf8p8uOaZMwPBlqCCV04jU2VZitmc2j4tCjkhvkCNVyfsl1IxI2fs3muFXFC0Q=", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("ThirdActivity", "encode apikey error");
        }

        queryInput = findViewById(R.id.editText_loca);
//        resultTextView = findViewById(R.id.textView2);
        listView_local = findViewById(R.id.listView_local);
        listView_local.setVisibility(View.INVISIBLE);
        loca = new ArrayList<City>();
    }

    public void search(View view) {

        Toast.makeText(weather_location_Activity.this, "Đang Tìm Kiếm... ", Toast.LENGTH_LONG).show();
        listView_local.setVisibility(View.VISIBLE);
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
                            loca.add(new City(queryInput.getText().toString(),addressDetail.getCountry(),"en-GB" ));


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

                String data = String.valueOf(loca.get(i).getCity()+loca.get(i).getCountry());

                WeatherPosition position = new WeatherPosition();
                position.setCity(loca.get(i).getCity());
                position.setCountry(loca.get(i).getCountry());
                position.setLocale(loca.get(i).getLocale());

                getweather(position);

                Log.e("ThirdActivitty", "onSearchError is: " + data);
                Toast.makeText(weather_location_Activity.this, "Bạn đã chọn " + data, Toast.LENGTH_SHORT).show();
                listView_local.setVisibility(View.INVISIBLE);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.d("localweather+ :/ ", String.valueOf(position));

        Awareness.getCaptureClient(this).getWeatherByPosition(position).addOnSuccessListener(weatherStatusResponse-> {
                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
                    Situation situation = weatherSituation.getSituation();
//                // Callback listener for execution success.
//                .addOnSuccessListener(weatherStatusResponse -> {
//                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
//                    WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
//                    Situation situation = weatherSituation.getSituation();

            String weatherInfoStr =  "\n" +
                    "Temperature is " + situation.getTemperatureC() + "℃" +
                    "," + situation.getTemperatureF() + "℉" + "\n" +
                    "Wind speed is " + situation.getWindSpeed() + "km/h" + "\n" +
                    "Wind direction is " + situation.getWindDir() + "\n" +
                    "Humidity is " + situation.getHumidity() + "%" + "\n" ;
            Log.d("localweather1", String.valueOf(weatherStatus));
            Log.d("localweather2", String.valueOf(situation));
            Log.d("localweather3", weatherInfoStr);
                });
    }
}