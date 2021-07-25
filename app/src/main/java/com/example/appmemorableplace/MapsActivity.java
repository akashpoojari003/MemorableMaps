 package com.example.appmemorableplace;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

 public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {
    LocationManager locationManager;
    LocationListener locationListener;

    private GoogleMap mMap;

    public void centerMapOnLoc(Location location , String title){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        }else{
            Toast.makeText(this, "Failed to load your location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0 , 0 , locationListener);
                Location lastKnownLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLoc(lastKnownLoc,"Your Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if(intent.getIntExtra("Number", 0)==0){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    centerMapOnLoc(location,"Your Location");
                }
            };
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0 , 0 , locationListener);
                Location lastKnownLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLoc(lastKnownLoc,"Your Location");
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            }
        }else{
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.loc.get(intent.getIntExtra("Number",0)).latitude);
            placeLocation.setLongitude(MainActivity.loc.get(intent.getIntExtra("Number",0)).longitude);
            centerMapOnLoc(placeLocation,MainActivity.places.get(intent.getIntExtra("Number",0)));
        }


    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listAdd = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if( listAdd!=null && listAdd.size()>0){
                if(listAdd.get(0).getThoroughfare()!=null){
                    if(listAdd.get(0).getSubThoroughfare()!=null){
                        address += listAdd.get(0).getSubThoroughfare()+" ";
                    }
                    address += listAdd.get(0).getThoroughfare();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(address.equals("")){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address += sdf.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.loc.add(latLng);
        MainActivity.places.add(address);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.appmemorableplace",Context.MODE_PRIVATE);
        try {
            ArrayList<String> places = new ArrayList<>();
            ArrayList<String> latitude = new ArrayList<>();
            ArrayList<String> longitude = new ArrayList<>();
            for(LatLng cord : MainActivity.loc){
                latitude.add(Double.toString(cord.latitude));
                longitude.add(Double.toString(cord.longitude));
            }
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latti",ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longi",ObjectSerializer.serialize(longitude)).apply();
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
    }
}