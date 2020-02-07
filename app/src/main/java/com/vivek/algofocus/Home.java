package com.vivek.algofocus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {

    FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference database;
    Location lastlocation;
    TextView home_name, home_mail, home_mob_no, home_address;
    private String UrlToImg;
    ImageView img_Profile;
    Button SignOut;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");
        home_name = findViewById(R.id.home_name);
        home_mail = findViewById(R.id.home_mail);
        home_mob_no = findViewById(R.id.home_mob_no);
        home_address = findViewById(R.id.home_address);
        img_Profile = findViewById(R.id.Img_profile);
        SignOut = findViewById(R.id.buttonsignout);
        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Home.this, Login.class));
                finish();
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                mGoogleApiClient = new GoogleApiClient.Builder(Home.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(Home.this)
                        .addOnConnectionFailedListener(Home.this).build();
                mGoogleApiClient.connect();
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);



            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Home.this, "Permission Denied", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_LONG).show();
                        lastlocation = location;
                        double latitude = lastlocation.getLatitude();
                        double longitue = lastlocation.getLongitude();
                        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitue, 1);
                            if (addresses.size() > 0) {
                                String address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                home_address.setText(address1);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    public void displayProfile() {
        database = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(getApplicationContext(), "id" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString(), Toast.LENGTH_SHORT).show();
        database.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    home_name.setText(dataSnapshot.child("Name").getValue().toString());
                    String Email = dataSnapshot.child("Email").getValue().toString();
                    if (!Email.isEmpty()) {

                        home_mail.setText(Email.trim());

                    }
                    else
                    {
                        home_mail.setText("Not Exist");
                    }
                    String Contact_number = dataSnapshot.child("Contact number").getValue().toString();
                    if (!Contact_number.isEmpty()) {

                        home_mob_no.setText(Contact_number.trim());

                    }
                    else
                    {
                        home_mob_no.setText("Not Exist");
                    }
                    UrlToImg = dataSnapshot.child("urlToImage").getValue().toString();
                    if (!UrlToImg.isEmpty()) {

                        Picasso.with(getApplicationContext()).load(UrlToImg).transform(new CropCircleTransformation()).into(img_Profile);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Data does not exit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Data loading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onResult(@NonNull Result locationSettingsResult) {
       final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                getLocation();
                displayProfile();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                     status.startResolutionForResult(Home.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are unavailable so not possible to show any dialog now
                        break;
                }

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Home.this, Login.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Home.this, Login.class));
                finish();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        // optional depending on your needs
    }


}

