package com.example.aluno.perigoalert;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity implements LocationListener {

    private static final String TAG = "Entrou";
    Intent intent;
    private LocationManager locationManager ;
    private String provider ;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Button btnMapa = (Button) findViewById(R.id.btnMapa);

        btnMapa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               intent = new Intent(Inicio.this, MapsActivity.class);
               startActivity(intent);
           }
        });


    }

    // Quando mudar a localização irá executar esse método
    @Override
    public void onLocationChanged ( Location location ) {

    }

    @Override
    public void onProviderEnabled ( String provider ) {

    }

    @Override
    public void onProviderDisabled ( String provider ) {

    }

    @Override
    public void onStatusChanged ( String provider , int status , Bundle extras ) {

    }


}
