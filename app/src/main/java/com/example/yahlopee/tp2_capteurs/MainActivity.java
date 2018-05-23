package com.example.yahlopee.tp2_capteurs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private LocationManager gps;
    private LocationListener gpsListener;
    private TextView la, lo,di;
    private float dist;
    private Location [] distances;
    private float dists;
    private Location crntlocation, newLocation;
    private boolean cont = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        la = (TextView) findViewById(R.id.lat);
        lo = (TextView) findViewById(R.id.lo);
        di = (TextView) findViewById(R.id.dista);
        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                double lat = location.getLatitude();
                double lon = location.getLongitude();
                la.setText("Latitude: "+ lat);
                lo.setText("Longitude: " + lon);
                if(!cont){
                    crntlocation =new Location("crntLocations");
                    crntlocation.setLatitude(lat);
                    crntlocation.setLongitude(lon);
                    cont = true;
                }else{
                    newLocation = new Location("newLocation");
                    newLocation.setLongitude(lon);
                    newLocation.setLatitude(lat);
                    dists = crntlocation.distanceTo(newLocation);
                    di.setText("Distance parcouru: "+dists+"Mts");
                    cont = false;

                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else{
            gps.requestLocationUpdates(gps.GPS_PROVIDER, 15000, 5, gpsListener);
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                gps.requestLocationUpdates(gps.GPS_PROVIDER, 15000, 5, gpsListener);
            }
        }else {
            finish();
            System.exit(0);
        }
    }
}
