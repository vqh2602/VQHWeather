package com.example.vqhcovid.weather;

public class Weather24 {
    public String time;
    public String day_night;
    public String temp;
    public String rain;
    public int urlimage;

    public Weather24(String time, String day_night, String temp, String rain, int urlimage) {
        this.time = time;
        this.day_night = day_night;
        this.temp = temp;
        this.rain = rain;
        this.urlimage = urlimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay_night() {
        return day_night;
    }

    public void setDay_night(String day_night) {
        this.day_night = day_night;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public int getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(int urlimage) {
        this.urlimage = urlimage;
    }
}
