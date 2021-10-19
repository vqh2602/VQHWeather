package com.example.vqhcovid.City;

public class City {
    private String City;
    private String Country;
    private String Locale;
    private String address;


    public City(String city, String country, String locale, String address) {
        City = city;
        Country = country;
        Locale = locale;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    @Override
    public String toString() {
        return " Địa Chỉ: "+this.getAddress()+"\nThành Phố: "+this.getCity() +"\nQuốc Gia: "+ this.getCountry()+"";}
}
