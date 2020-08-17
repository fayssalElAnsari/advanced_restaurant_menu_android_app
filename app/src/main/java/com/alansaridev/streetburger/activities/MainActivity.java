package com.alansaridev.streetburger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.alansaridev.streetburger.R;
import com.alansaridev.streetburger.fragment.BuyFragment;
import com.alansaridev.streetburger.fragment.HomeFragment;
import com.alansaridev.streetburger.fragment.StoresFragment;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    final Fragment homeFragment = new HomeFragment();
    final Fragment storesFragment = new StoresFragment();
    final Fragment buyFragment = new BuyFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;


    private static final int REQUEST_FINE_LOCATION = 9001;
    private static final int REQUEST_COARSE_LOCATION = 9002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm.beginTransaction().add(R.id.main_container, buyFragment, "3").hide(buyFragment).commit();
        fm.beginTransaction().add(R.id.main_container, storesFragment, "2").hide(storesFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
//                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        checkInternetConnectivity();
                        break;
                    case R.id.action_places:
//                        Toast.makeText(MainActivity.this, "Nos Magazins", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(storesFragment).commit();
                        active = storesFragment;
                        askForLocationPermission();
                        askToActivateGPS();
                        break;
//                    case R.id.action_favourites:
////                        Toast.makeText(MainActivity.this, "Favoris", Toast.LENGTH_SHORT).show();
//                        fm.beginTransaction().hide(active).show(homeFragment).commit();
//                        active = homeFragment;
//                        break;
                    case R.id.action_order:
//                        Toast.makeText(MainActivity.this, "Commander", Toast.LENGTH_SHORT).show();
                        fm.beginTransaction().hide(active).show(buyFragment).commit();
                        active = buyFragment;
//                        askForPhonePermission();
                        break;
                }

                return true;
            }
        });

    }

    private void checkInternetConnectivity() {
        if (isInternetAvailable() == true) {
            return;
        } else {

        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForLocationPermission() {

        /// this code needs to be copied to on btn clicked of the main activity fragment
        //this part inside on create
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        } else {
            //request location permission
            Log.d(TAG, "onMapReady: location permission not given");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.d(TAG, "requestLocationPermission: corase location needed");
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Cette permission est necessaire pour trouver les magasins les plus proches de votre position")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            Log.d(TAG, "requestLocationPermission: asking for coarse location");
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(TAG, "requestLocationPermission: fin location needed");
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Cette permission est necessaire pour determiner la route entre votre position et le magasin")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            Log.d(TAG, "requestLocationPermission: asking for fine location");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        if (requestCode == REQUEST_FINE_LOCATION) {
            Log.d(TAG, "onRequestPermissionsResult: fine location");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(context, "Fine Location Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "Fine Location Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_COARSE_LOCATION) {
            Log.d(TAG, "onRequestPermissionsResult: coarse location");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(context, "Coarse Location Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "Coarse Location Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i(TAG, "onActivityResult: GPS Enabled by user");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(TAG, "onActivityResult: User rejected GPS request");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    // the end of what needs to be copied


    private void askToActivateGPS() {
    }

    private void askForPhonePermission() {
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


}