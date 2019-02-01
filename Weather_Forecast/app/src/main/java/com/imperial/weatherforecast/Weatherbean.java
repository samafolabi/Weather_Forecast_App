package com.imperial.weatherforecast;

/**
 * Created by riteshranjan on 08/05/15.
 */


public class Weatherbean {


    private String country = "county";
    private String temperature = "temperature";
    private String humidity = "humidity";
    private String pressure = "pressure";
    private String urlString = null;

    private boolean gotResponse ;

    public Weatherbean(String url){

        this.urlString = url;
    }
    public Weatherbean(){


    }

    public String getCountry() {
        return country;
    }

    public boolean isGotResponse() {
        return gotResponse;
    }

    public void setGotResponse(boolean gotResponse) {
        this.gotResponse = gotResponse;
    }


    public String getTemperature() {

        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getUrlString() {
        return urlString;
    }

    public String getPressure() {
        return pressure;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}
