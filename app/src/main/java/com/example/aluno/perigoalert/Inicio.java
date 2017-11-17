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
        Button btnAddPerigo = (Button) findViewById(R.id.btnAddPerigo);


        //Determina que forma será feita a localização
        //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE );
        //Criteria criteria = new Criteria ();
        //provider = locationManager.GPS_PROVIDER;
        //try{
          //  location = locationManager.getLastKnownLocation(provider);
            //Log.i(TAG,"ATÉ AQUI RODOU");
        //}catch (SecurityException e){
          //  e.printStackTrace();
        //}

        //Quando a pessoa clicar no botão a outra activy será aberta
        btnMapa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               intent = new Intent(Inicio.this, MapsActivity.class);
               //intent.putExtra("lat", location.getLatitude()); <-- POR ENQUANTO NÃO CONSEGUE LOCALIZAR O LOCAL ATUAL
               //intent.putExtra("lng", location.getLongitude());
               startActivity(intent);
           }
        });

        btnAddPerigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Inicio.this, AddPerigo.class);
                //intent.putExtra("lat", location.getLatitude()); <-- POR ENQUANTO NÃO CONSEGUE LOCALIZAR O LOCAL ATUAL
                //intent.putExtra("lng", location.getLongitude());
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
