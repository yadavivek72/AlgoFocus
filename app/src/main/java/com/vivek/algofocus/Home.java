package com.vivek.algofocus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    Location lastlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);
        PermissionListener permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(Home.this,"Permission Granted",Toast.LENGTH_SHORT).show();
                getLocation();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Home.this,"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        };

        TedPermission.with(Home.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    void getLocation() {
        Log.d("TAGLOC", "getLocation: permissions granted");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        Toast.makeText(getApplicationContext(),"not null",Toast.LENGTH_LONG).show();
                        lastlocation = location;
                        double latitude = lastlocation.getLatitude();
                        double longitue = lastlocation.getLongitude();
                        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitue, 1);
                            if (addresses.size() > 0) {
                                String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                Toast.makeText(Home.this,"Address"+address1,Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }



}
