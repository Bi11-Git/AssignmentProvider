package gr.hua.dit.android.assignmentprovider;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import gr.hua.dit.android.assignmentprovider.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int GEOFENCING_FINE_LOCATION_PERMISSION_CODE = 3;
    private static final float GEOFENCE_RADIUS = 200;
    private static final int ENABLE_MYLOCATION_FINE_LOCATION_PERMISSION_CODE = 4;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    GeofencingClient geofencingClient;
    private Geofence geofence;
    private PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(MapsActivity.this);

    }

    private void addGeofences() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, GEOFENCING_FINE_LOCATION_PERMISSION_CODE);
            return;
        }

        geofencingClient.addGeofences(geofencingRequest(), getGeofencePendingIntent())
        .addOnSuccessListener(MapsActivity.this, unused -> {
            Log.d("geofencingClient", "Geofence successfully added");
        })
        .addOnFailureListener(MapsActivity.this, unused -> {
            Log.d("geofencingClient", "Geofence addition failed");
        });

    }

    private PendingIntent getGeofencePendingIntent() {
        if(geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent= new Intent(this, GeofenceBroadcastReceiver.class);

        geofencePendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest geofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GEOFENCING_FINE_LOCATION_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addGeofences();
                }
                break;
            case ENABLE_MYLOCATION_FINE_LOCATION_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Athens and move the camera
        LatLng marker = new LatLng(37.9715, 23.7267);    //Athens
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));

        mMap.setOnMapLongClickListener(latLng -> {
            geofence = new Geofence.Builder()
                    .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setRequestId("GeofenceId")
                    .build();


            CircleOptions circleOptions = new CircleOptions();
            circleOptions.radius(GEOFENCE_RADIUS);
            circleOptions.center(latLng);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(64, 255, 0, 0));
            circleOptions.strokeWidth(4);


            mMap.clear();
            mMap.addCircle(circleOptions);

            addGeofences();
        });

        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Ask for permission

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //we need to show user a dialog for displaying why permission is needed
                // and then ask for the permission

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, GEOFENCING_FINE_LOCATION_PERMISSION_CODE);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, GEOFENCING_FINE_LOCATION_PERMISSION_CODE);

            }
        }

        mMap.setMyLocationEnabled(true);
    }
}