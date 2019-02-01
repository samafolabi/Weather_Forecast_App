package com.imperial.weatherforecast;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String url2 = "&units=imperial&mode=json&APPID=fe6e6084cd89fee4a4bb1f1071df7ae5";
    EditText location;
    TextView countryLabel, temperatureLabel, humidityLabel, pressureLabel;
    Button button;
    private Weatherbean obj;
    private XmlPullParserFactory xmlFactory;
    private FirebaseAnalytics mFirebaseAnalytics;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        location = (EditText) findViewById(R.id.editText1);
        countryLabel = (TextView) findViewById(R.id.textView2);
        temperatureLabel = (TextView) findViewById(R.id.textView3);
        humidityLabel = (TextView) findViewById(R.id.textView4);
        pressureLabel = (TextView) findViewById(R.id.textView5);
        button = (Button) findViewById(R.id.button1);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //WebView.setWebContentsDebuggingEnabled();
        /*MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdSize(AdSize.SMART_BANNER);*/

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6391424078600803/8531526579");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
        //location.setText("\\u00A0\\u00A0\\u00A0\\u00A0\\u00A0" + this);

        toggleFieldsVisibility(false);

        location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                        button.performClick();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void toggleFieldsVisibility(Boolean visible) {
        int state = (visible) ? View.VISIBLE : View.INVISIBLE;

        countryLabel.setVisibility(state);
        temperatureLabel.setVisibility(state);
        humidityLabel.setVisibility(state);
        pressureLabel.setVisibility(state);

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice("QTCYFV70F305013B7")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void open(View view) {
        String url = location.getText().toString();
        if (!url.equals("")) {
            String finalUrl = url1 + url + url2;
            //Log.d("Final url", finalUrl);
            obj = new Weatherbean(finalUrl);

            new FetchWeather(obj.getUrlString(), context).execute();

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

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
                //Log.d("result",result);

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
                    //Log.d("Result",result);
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
                    //Log.d("Exception","True");
                    weatherObj.setGotResponse(false);
                    e.printStackTrace();
                }
            }
            if (weatherObj.isGotResponse()) {
                String countryText, temperatureText, humidityText, pressureText;/*, countryForCode = null;
                
                try {
                    countryForCode = CountryJSON.getCountryForCode(weatherObj.getCountry());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                countryText = getString(R.string.country) + countryForCode;*/
                countryText = getString(R.string.country) + weatherObj.getCountry();
                temperatureText = getString(R.string.temperature) + weatherObj.getTemperature() + getString(R.string.fahrenheit);
                humidityText = getString(R.string.humidity) + weatherObj.getHumidity() + getString(R.string.percent);
                pressureText = getString(R.string.pressure) + weatherObj.getPressure() + getString(R.string.pascal);

                countryLabel.setText(countryText);
                temperatureLabel.setText(temperatureText);
                humidityLabel.setText(humidityText);
                pressureLabel.setText(pressureText);
                toggleFieldsVisibility(true);
            } else {
                Toast toast = Toast.makeText(context, "Could not fetch weather info for the input city.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                location.setText("");
                toggleFieldsVisibility(false);
            }

        }
    }
}
