package com.example.aluno.perigoalert;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ecari on 17/11/2017.
 */

public class LocationData {

    LatLng local;
    public double score;

    public LocationData(LatLng local, double score) {
        this.local = local;
        this.score = score;
    }
}
