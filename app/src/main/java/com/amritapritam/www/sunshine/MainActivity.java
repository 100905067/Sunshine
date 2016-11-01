package com.amritapritam.www.sunshine;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new PlaceholderFragment())
                    .commit();
        }
    }

    public static class PlaceholderFragment extends Fragment{

        public PlaceholderFragment(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            ArrayAdapter<String> mforecastAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_forecast,
                            R.id.list_item_forecast_textview,
                            weekForecast);
            forecastData.setAdapter(mforecastAdapter);
            return rootview;
        }
    }
}
