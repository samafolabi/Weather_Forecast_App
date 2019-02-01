package com.imperial.weatherforecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HP on 10/19/2016.
 */

public class CountryCodeJSON {

    private String countryCodes = "{ \"countries\" :[" +
            "{\"countryCode\": \"GB\", " +
            "\"country\": \"Great Britain\"}" +
            "] }";





    public String getCountryForCode(String code) throws JSONException {
        JSONObject countriesObj = new JSONObject(countryCodes);
        JSONArray countriesArray = countriesObj.optJSONArray("countries");
        String finalCountry = null;
        for (int i = 0; i < countriesArray.length(); i++){
            JSONObject countryObj = countriesArray.getJSONObject(i);
            String countryCode = countryObj.optString("countryCode");
            String country = countryObj.optString("country");
            if (code != countryCode) {continue;}
            else {
                finalCountry = country;
                break;
            }
        }
        return finalCountry;
    }


}
