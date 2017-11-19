package com.example.aluno.perigoalert;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker currentLocationMaker;
    private LatLng currentLocationLatLong;
    public DatabaseReference mDataBase;
    LatLng localizacaoAtual = new LatLng(-22.9460577, -46.5262442);

    private static final String TAG = "Entrou";

    private double latitude=0, longitude=0;
    private List<double[]> dadosObtidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startGettingLocations();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        getMarkers();
    }
    public boolean verificador = false;
    public String repeteMarcador="";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        Log.i(TAG,"MAPS ACTIVITY FUNCIONANDO");

        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(localizacaoAtual).title("Localização atual"));


        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(localizacaoAtual).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {

                showVoto(marker);
                return false;
            }
        });



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                insertMarker(point);
            }
        });

    }

    public void showVoto(final Marker marker){

        final Dialog dialog = new Dialog(this);
        dialog.setTitle(marker.getTitle());

        dialog.setContentView(R.layout.alertdialog_votacao);

        Button verdadeira = (Button) dialog.findViewById(R.id.verdadeira);
        Button falsa = (Button) dialog.findViewById(R.id.falsa);

        verdadeira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataBase.child("location").child(marker.getTitle()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                if (dataSnapshot.getValue() != null) {
                                    atualizaScore((Map<String, Object>) dataSnapshot.getValue(), marker, true);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
                dialog.dismiss();

            }
        });

        falsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDataBase.child("location").child(marker.getTitle()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                if (dataSnapshot.getValue() != null) {
                                    atualizaScore((Map<String, Object>) dataSnapshot.getValue(), marker,false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
                dialog.dismiss();

            }
        });

       dialog.show();



    }

    private void atualizaScore(Map<String,Object> locations, Marker marker, boolean verdadeiro) {

        for (Map.Entry<String, Object> entry : locations.entrySet()){

            Map singleLocation = (Map) entry.getValue();
            LatLng latLng = new LatLng((Double) singleLocation.get("latitude"), (Double)singleLocation.get("longitude"));
            Long score = null;
            score = (Long) singleLocation.get("score");
            if (verdadeiro)  mDataBase.child("location").child(marker.getTitle()).child(String.valueOf(marker.getSnippet())).child("score").setValue(score+1);
            else mDataBase.child("location").child(marker.getTitle()).child(String.valueOf(marker.getSnippet())).child("score").setValue(score-1);


        }


    }


    //  QUANDO É FEITO A MUDANÇA DE LOCALIZAÇÃO
    @Override
    public void onLocationChanged(Location location) {
        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        //Add marker
        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.title("Localização atual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.clear();
        currentLocationMaker = mMap.addMarker(markerOptions);
        getMarkers();

        //Move to new location
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        Toast.makeText(this, "Localização atualizada", Toast.LENGTH_SHORT).show();
        getMarkers();
    }

    //FUNÇÃO USADA NO startGettingLocations()
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    //FUNÇÃO USADA NO findUnAskedPermissions()
    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    //FUNÇÃO USADA NO hasPermission()
    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    // PERMITIR ATIVAÇÃO DO GPS
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    // BUSCA A LOCALIZAÇÃO ATUAL
    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000 * 10;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Não é possível obter a localização", Toast.LENGTH_SHORT).show();
        }
    }

    // MOSTRA TODAS AS LOCALIZAÇÕES ENCONTRADAS NO FIREBASE
    private void getMarkers(){

        mDataBase.child("location").child("Roubo").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)

                            getAllLocations((Map<String,Object>) dataSnapshot.getValue(), "Roubo");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        mDataBase.child("location").child("Assassinato").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue(), "Assassinato");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        mDataBase.child("location").child("Ponto de Drogas").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue(), "Ponto de Drogas");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        mDataBase.child("location").child("Roubo de Automoveis").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue(), "Roubo de Automoveis");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        mDataBase.child("location").child("Estupro").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue(), "Estupro");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    //FUNÇÃO USADA NO getMarkers()
    private void getAllLocations(Map<String,Object> locations, String perigo) {

        for (Map.Entry<String, Object> entry : locations.entrySet()){

            //Date newDate = new Date(Long.valueOf(entry.getKey()));
            Map singleLocation = (Map) entry.getValue();
            LatLng latLng = new LatLng((Double) singleLocation.get("latitude"), (Double)singleLocation.get("longitude"));
            Long score = (Long) singleLocation.get("score");
            if (score.intValue() > -10) addMarkerPerigo(entry.getKey().toString(), latLng, perigo);


        }


    }




    // FUNÇÃO USADA NO getAllLocations()
    private void addMarkerPerigo(String newDate, LatLng latLng, String perigo) {
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(perigo);
        markerOptions.snippet(newDate);

        if (perigo == "Roubo") markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (perigo == "Assassinato") markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        if (perigo == "Ponto de Drogas") markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        if (perigo == "Roubo de Automoveis") markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        if (perigo == "Estupro") markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        mMap.addMarker(markerOptions);

    }

    EditText input;


    // ADICIONA UM NOVO MARCADOR QUANDO CLICA NA TELA
    public void insertMarker(final LatLng local){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("O que aconteceu? ");
        dialog.setContentView(R.layout.alertdialog_incidente);
        dialog.setCancelable(true);

        final RadioButton rd0 = (RadioButton) dialog.findViewById(R.id.rboRoubo);
        final RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rboAssassinato);
        final RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rboPDrogas);
        final RadioButton rd3 = (RadioButton) dialog.findViewById(R.id.rboRAutomoveis);
        final RadioButton rd4 = (RadioButton) dialog.findViewById(R.id.rboEstupro);

        Button cancelar = (Button) dialog.findViewById(R.id.cancelar);

        Button confirmar = (Button) dialog.findViewById(R.id.confirmar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mNovo;
                if(rd0.isChecked()){
                    mNovo = mDataBase.child("location").child("Roubo").child(String.valueOf( new Date().getTime()));
                    mNovo.setValue(local);
                    mNovo.child("score").setValue(0);
                }
                if (rd1.isChecked()){
                    mNovo = mDataBase.child("location").child("Assassinato").child(String.valueOf( new Date().getTime()));
                    mNovo.setValue(local);
                    mNovo.child("score").setValue(0);
                }
                if (rd2.isChecked()){
                    mNovo = mDataBase.child("location").child("Ponto de Drogas").child(String.valueOf( new Date().getTime()));
                    mNovo.setValue(local);
                    mNovo.child("score").setValue(0);
                }
                if (rd3.isChecked()) {
                    mNovo = mDataBase.child("location").child("Roubo de Automoveis").child(String.valueOf( new Date().getTime()));
                    mNovo.setValue(local);
                    mNovo.child("score").setValue(0);
                }
                if (rd4.isChecked()){
                    mNovo = mDataBase.child("location").child("Estupro").child(String.valueOf( new Date().getTime()));
                    mNovo.setValue(local);
                    mNovo.child("score").setValue(0);
                }
                getMarkers();
                dialog.dismiss();
            }

        });

        dialog.show();




    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
