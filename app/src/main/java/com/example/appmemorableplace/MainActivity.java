package com.example.appmemorableplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<LatLng> loc = new ArrayList<>();
    static ArrayList<String> places = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.appmemorableplace", Context.MODE_PRIVATE);
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longitude = new ArrayList<>();
        places.clear();
        latitude.clear();
        longitude.clear();
        loc.clear();
        try {
            places = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitude = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latti",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longi",ObjectSerializer.serialize(new ArrayList<String>())));

        }catch (Exception e){
            e.printStackTrace();
        }
        if(places.size()>0 && latitude.size()>0 && longitude.size()>0){
            if(places.size()==latitude.size() && places.size()==longitude.size()){
                for(int i =0 ; i<latitude.size() ; i++){
                    loc.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }else{
            places.add("Add a new place...");
            loc.add(new LatLng(0,0));
        }

         arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1 , places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext() , MapsActivity.class);

                intent.putExtra("Number",position);
                startActivity(intent);

            }
        });

    }
}