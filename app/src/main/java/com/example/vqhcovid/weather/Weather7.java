package com.example.vqhcovid.weather;

public class Weather7 {
    public String date;
    public String troi;
    public String min;
    public String max;
    public String sunset;
    public String sunrise;
    public int imageUrl;

    public Weather7(String date, String troi, String min, String max, String sunset, String sunrise, int imageUrl) {
        this.date = date;
        this.troi = troi;
        this.min = min;
        this.max = max;
        this.sunset = sunset;
        this.sunrise = sunrise;
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTroi() {
        return troi;
    }

    public void setTroi(String troi) {
        this.troi = troi;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
