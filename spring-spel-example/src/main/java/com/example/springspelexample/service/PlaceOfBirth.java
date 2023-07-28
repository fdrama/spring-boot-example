package com.example.springspelexample.service;

/**
 * @author fdrama
 * date 2023年07月26日 14:27
 */
public class PlaceOfBirth {
    private String city;
    private String country;

    public PlaceOfBirth(String city) {
        this.city=city;
    }
    public PlaceOfBirth(String city, String country)
    {
        this(city);
        this.country = country;
    }


    public String getCity() {
        return city;
    }
    public void setCity(String s) {
        this.city = s;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }


}
