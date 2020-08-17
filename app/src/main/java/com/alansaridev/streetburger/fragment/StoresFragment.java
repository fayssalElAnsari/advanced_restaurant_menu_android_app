package com.alansaridev.streetburger.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alansaridev.streetburger.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.alansaridev.streetburger.util.Constants.MAPVIEW_BUNDLE_KEY;

public class StoresFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final String TAG = StoresFragment.class.getName();
    private static final int REQUEST_FINE_LOCATION = 9001;
    private static final int REQUEST_COARSE_LOCATION = 9002;
    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Context context;

    String restaurantName;

    private FirebaseDatabase database;
    private DatabaseReference storesRef;
    private DatabaseReference restaurantRef;

    final ArrayList<MarkerOptions> storesMarkerOptionsList = new ArrayList<MarkerOptions>();


    public StoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    public static StoresFragment newInstance(String param1, String param2) {
        StoresFragment fragment = new StoresFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stores, container, false);
        getLastKnownLocation();
        mMapView = view.findViewById(R.id.stores_map);
        initGoogleMap(savedInstanceState);

        // for location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        // for gps
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // now we ask user to enable gps
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });

        // initialise variables
        restaurantName = "street burger";

        return view;
    }

    public void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called");
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(context, "permission de position est necessaire pour activer cette fonction de l'application!", Toast.LENGTH_SHORT).show();
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
//        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()) {
//                    Location location = task.getResult();
//                    if (location != null) {
//                        Toast.makeText(context, "latitude:" + location.getLatitude() + "; altitude: " + location.getAltitude(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(context, "couldn't get location: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        //get the list of stores from realtime database
        // this list will contain markeroptions that will
        // be added on map ready callback
        database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);

        restaurantRef = database.getReference("restaurants/" + restaurantName);
        storesRef = restaurantRef.child("stores");

        storesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: getting stores list...");
                for (DataSnapshot storeElement : snapshot.getChildren()) {
                    // store name
                    String storeName = storeElement.child("name").getValue().toString();
                    Log.d(TAG, "onDataChange: store name is: " + storeName);
                    //store position
                    String storeLocationStr = storeElement.child("geopoint").getValue().toString();
                    String[] latLong = storeLocationStr.split(",");
                    LatLng storeGeopoint = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));
                    //store info
                    boolean storeOpen = (boolean) storeElement.child("open").getValue();
                    String storeOpenStr;
                    if (storeOpen) {
                        storeOpenStr = "ouvert";
                    } else {
                        storeOpenStr = "fermee";
                    }

                    String storePhoneNumber = storeElement.child("phone number").getValue().toString();
                    String storeSnippetStr = "telephone: " + storePhoneNumber + "; " + storeOpenStr;
                    MarkerOptions storeMarkerOptions = new MarkerOptions().position(storeGeopoint).title(storeName).snippet(storeSnippetStr);
                    storesMarkerOptionsList.add(storeMarkerOptions);
                    Log.d(TAG, "onDataChange: added to map " + storeMarkerOptions.getTitle().toString());

                    boolean cameraAnimated = false;
                    Log.d(TAG, "onMapReady: storemarkerOptionsList size is:" + storesMarkerOptionsList.size());
                    for (MarkerOptions markerOptionsElement : storesMarkerOptionsList) {
                        Marker marker = map.addMarker(markerOptionsElement);
                        // animate camera to the first store in the db if not animated already
                        if (!cameraAnimated) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 14.0f));
                            cameraAnimated = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        } else {
            //request location permission
            Log.d(TAG, "onMapReady: location permission not given");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
        map.setMyLocationEnabled(true);


        // for each store location in the list add a marker in the map

    }


    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.d(TAG, "requestLocationPermission: corase location needed");
            new AlertDialog.Builder(context)
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
            new AlertDialog.Builder(context)
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(context, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
}