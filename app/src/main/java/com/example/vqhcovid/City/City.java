package com.example.vqhcovid.City;

public class City {
    private String City;
    private String Country;
    private String Locale;

    public City(String city, String country, String locale) {
        City = city;
        Country = country;
        Locale = locale;
    }


    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getLocale() {
        return Locale;
    }

    public void setLocale(String locale) {
        Locale = locale;
    }
}
