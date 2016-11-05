package com.amritapritam.www.sunshine;


import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    ArrayAdapter<String> mforecastAdapter;
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container,false);
        ListView forecastData = (ListView) rootview.findViewById(R.id.listview_forecast);
        String[] list = {
                "Today-Sunny-88/63",
                "Tomorrow-Rainy-90/23",
                "Weds-Cloudy-72/63",
                "Thurs-Rainy-64/51",
                "Friday-Foggy-70/46",
                "Sat-Sunny-76/68"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(list));
        mforecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
        forecastData.setAdapter(mforecastAdapter);
        return rootview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_refresh){
            FetchWeatherTask fetch = new FetchWeatherTask();
            fetch.execute("94587");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {

            if(params.length==0)
                return null;

            String forecast_base_url = "http://api.openweathermap.org/data/2.5/forecast/daily";
            String QUERY_PARAM = "q";
            String FORMAT_PARAM = "metric";
            String UNITS_PARAM = "units";
            String NUMDAYS_PARAM = "cnt";
            String APPID_PARAM = "appid";

            String format="json";
            String units="imperial";
            int numOfDays= 7;
            String appid="8ec3485e9f2f65f1891676b6382c0c97";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {
                Uri uri = Uri.parse(forecast_base_url).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(NUMDAYS_PARAM,Integer.toString(numOfDays))
                        .appendQueryParameter(APPID_PARAM,appid).build();

                URL url =  new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            JSONObject data = null;
            JSONArray list  = null;
            try {
                data = new JSONObject(forecastJsonStr);
                list = data.getJSONArray("list");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] weatherForecats = new String[list.length()];
            for(int i=0;i<list.length();i++){
                try {
                    JSONObject json = list.getJSONObject(i);
                    long timestamp =  json.getLong("dt")*1000;
                    Date date = new Date (timestamp);
                    SimpleDateFormat month_date = new SimpleDateFormat("EEE,MMM dd");
                    String date1 = month_date.format(date);
                    JSONObject temp = json.getJSONObject("temp");
                    int min = temp.getInt("min");
                    int max = temp.getInt("max");
                    String weather = json.getJSONArray("weather").getJSONObject(0).getString("main");
                    weatherForecats[i] = date1+"-"+weather+"-"+max+"/"+min;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(weatherForecats!=null) {
                Log.d(LOG_TAG, Arrays.asList(weatherForecats).toString());
                return weatherForecats;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings!=null) {
                mforecastAdapter.clear();
                mforecastAdapter.addAll(strings);
            }
        }
    }

}
