package com.example.yahlopee.tp2_capteurs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private LocationManager gps;
    private Location location;
    private TextView la, lo, di, Nla, Nlo, status;
    private LinearLayout Nframe;
    private Button position;
    private String choix_source = "";
    private int cont =0;
    private boolean gps_enable = true;
    private boolean redem =false;
    private List<LatLng> points  = new ArrayList<LatLng>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        la = (TextView) findViewById(R.id.lat);
        lo = (TextView) findViewById(R.id.lo);
        di = (TextView) findViewById(R.id.dista);
        status = (TextView) findViewById(R.id.status);
        Nframe = (LinearLayout) findViewById(R.id.NLayout);
        Nla = (TextView) findViewById(R.id.latitudeN);
        Nlo = (TextView) findViewById(R.id.longitudeN);
        Nframe.setVisibility(View.INVISIBLE);
        position = (Button) findViewById(R.id.obtenirPosition);
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
            choisirSource();
        }

    }

    public void obtenirPosition(View view) {
       if(gps_enable){
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               // TODO: Consider calling
               //    ActivityCompat#requestPermissions
               // here to request the missing permissions, and then overriding
               //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
               //                                          int[] grantResults)
               // to handle the case where the user grants the permission. See the documentation
               // for ActivityCompat#requestPermissions for more details.
               return;
           }
           gps.requestLocationUpdates(choix_source, 15000, 0, this);
           Toast.makeText(this, "On est train de trouver votre position", Toast.LENGTH_SHORT).show();
       }

    }


    private void choisirSource() {

        //On demande au service la liste des sources disponibles.
        List<String> providers = gps.getProviders(true);
        final String[] sources = new String[providers.size()];
        int i = 0;
        //on stock le nom de ces source dans un tableau de string
        for(String provider : providers)
            sources[i++] = provider;
        //On affiche la liste des sources dans une fenêtre de dialog
        new AlertDialog.Builder(this)
                .setItems(sources, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //on stock le choix de la source choisi
                        choix_source = sources[which];
                        //on ajoute dans la barre de titre de l'application le nom de la source utilisé
                        setTitle(String.format("%s Choisez la source %s", getString(R.string.app_name),
                                choix_source));
                    }
                })
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                choisirSource();
            }
        }else {
            finish();
            System.exit(0);
        }
    }

    public double Distance(LatLng starP, LatLng endP){
        int radius = 6371;
        double lat1 = starP.latitude;
        double lat2 = endP.latitude;
        double lon1 = starP.longitude;
        double lon2 = endP.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon2);
        double p = Math.sin(dLat / 2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2)
                *Math.sin(dLon / 2);
        double unk = 2 * Math.asin(Math.sqrt(p));
        double valueR = radius * unk;
        double km = valueR /1;
        DecimalFormat newformat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newformat.format(km));
        double meter = valueR % 1000;
        int meterinDec = Integer.valueOf(newformat.format(meter));
        di.setText("Distance parcouru entre les 2 distances: "+valueR+" Km");
        return radius * unk;

    }

    public void arreter(View view){
        this.gps_enable = false;
        Toast.makeText(this, "Interruption du GPS", Toast.LENGTH_SHORT).show();
    }

    public void redemarrer(){
        this.gps_enable = true;
        Toast.makeText(this, "Redemarrage du GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gps_enable){

            status.setText("La position a changé.");
            this.location = location;
            cont++;
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            la.setText("latitude: "+lat);
            lo.setText("longitude: "+lon);
            if(redem){
                Distance(points.get(points.size() - 1), new LatLng(lat,lon));
                redem=false;
            }
            points.add(new LatLng(lat,lon));
        }

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        status.setText("Le statut de la source a changé "+cont);
    }

    @Override
    public void onProviderEnabled(String s) {
        status.setText("La source a été activé.");
    }

    @Override
    public void onProviderDisabled(String s) {
        status.setText("La source a été désactivé");
    }


}
