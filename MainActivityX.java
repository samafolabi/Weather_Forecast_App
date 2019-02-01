package com.imperial.weatherforecast;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityX extends AppCompatActivity {

    private Context context;
    private String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String url2 = "&mode=json&APPID=fe6e6084cd89fee4a4bb1f1071df7ae5";
    EditText location;
   // EditText country, temperature, humidity, pressure;
    TextView countryLabel, temperatureLabel, humidityLabel, pressureLabel;
    private Weatherbean obj;
    private XmlPullParserFactory xmlFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
       /* EditText editText = null;
        final ColorStateList colors = editText.getHintTextColors();
        editText.setTextColor(colors);*/
        location = (EditText) findViewById(R.id.editText1);
        /*country = (EditText) findViewById(R.id.editText2);
        temperature = (EditText) findViewById(R.id.editText3);
        humidity = (EditText) findViewById(R.id.editText4);
        pressure = (EditText) findViewById(R.id.editText5);*/
        countryLabel = (TextView) findViewById(R.id.textView2);
        temperatureLabel = (TextView) findViewById(R.id.textView3);
        humidityLabel = (TextView) findViewById(R.id.textView4);
        pressureLabel = (TextView) findViewById(R.id.textView5);

        /*int brown = getResources().getColor(R.color.brown);
        int blue = getResources().getColor(R.color.blue);
        int green = getResources().getColor(R.color.green);
        int red = getResources().getColor(R.color.red);

        countryLabel.setBackgroundColor(brown);
        temperatureLabel.setBackgroundColor(blue);
        humidityLabel.setBackgroundColor(green);
        pressureLabel.setBackgroundColor(red);*/


 /*       country.setKeyListener(null);
        temperature.setKeyListener(null);
        humidity.setKeyListener(null);
        pressure.setKeyListener(null);*/

        toggleFieldsVisibility(false);

    }

    public void toggleFieldsVisibility(Boolean visible) {
        int state = (visible) ? View.VISIBLE : View.INVISIBLE;

        //country.setVisibility(state);
        countryLabel.setVisibility(state);
       // temperature.setVisibility(state);
        temperatureLabel.setVisibility(state);
       // humidity.setVisibility(state);
        humidityLabel.setVisibility(state);
        pressureLabel.setVisibility(state);
       // pressure.setVisibility(state);

    }

    public void open(View view) {
        String url = location.getText().toString();
        if (!url.equals("")) {
            String finalUrl = url1 + url + url2;
            Log.d("Final url", finalUrl);
           //++ country.setText(finalUrl);
            obj = new Weatherbean(finalUrl);

            new FetchWeather(obj.getUrlString(), context).execute();

        } else {
            Toast.makeText(this, "Please enter some place name", Toast.LENGTH_LONG).show();
        }

    }

    private class FetchWeather extends AsyncTask<String, String, String> {
        private String urlStr;
        private Context context;
        private ProgressDialog progressDialog;

        FetchWeather(String url, Context context) {
            urlStr = url;
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Fetching weather details... please wait");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{
                HttpURLConnection con = null ;
                InputStream is = null;

                    con = (HttpURLConnection) ( new URL(urlStr).openConnection());
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.connect();

                    // Let's read the response
                    StringBuffer buffer = new StringBuffer();
                    is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    while (  (line = br.readLine()) != null )
                        buffer.append(line + "\r\n");

                    is.close();
                    con.disconnect();
                    result =  buffer.toString();
                Log.d("result",result);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            progressDialog.dismiss();
            Weatherbean weatherObj = new Weatherbean();
            if(result!=null)
            {
                try {
                    Log.d("Result",result);
                    JSONObject weatherResponseJson = new JSONObject(result);

                    weatherObj.setCountry(weatherResponseJson.getString("name"));

                    String mainStrng = weatherResponseJson.getString("main");

                    JSONObject mainObj = new JSONObject(mainStrng);

                    weatherObj.setTemperature(mainObj.getString("temp"));
                    weatherObj.setPressure(mainObj.getString("pressure"));
                    weatherObj.setHumidity(mainObj.getString("humidity"));


                    String sysString = weatherResponseJson.getString("sys");
                    JSONObject sysJson = new JSONObject(sysString);

                    weatherObj.setCountry(sysJson.getString("country"));
                    weatherObj.setGotResponse(true);
                }
                catch (JSONException e)
                {
                    Log.d("Exception","True");
                    weatherObj.setGotResponse(false);
                    e.printStackTrace();
                }
            }
            if (weatherObj.isGotResponse()) {

                countryLabel.setText(weatherObj.getCountry());
                temperatureLabel.setText(weatherObj.getTemperature());
                humidityLabel.setText(weatherObj.getHumidity());
                pressureLabel.setText(weatherObj.getPressure());
                toggleFieldsVisibility(true);
            } else {
                Toast toast = Toast.makeText(context, "Could not fetch weathe for the city. please enter proper city name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                location.setText("");
                toggleFieldsVisibility(false);
            }

        }
    }
}
