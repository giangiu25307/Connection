package com.ConnectionProject.connection.localization;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.ConnectionProject.connection.View.Connection;

public class GPS  implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    Connection connection;
    Location location;

    public GPS(Connection connection) {
        this.connection = connection;
        LocationManager locationManager = (LocationManager)
                connection.getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) connection.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(connection, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(connection, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }
    public Location getLocation(){
        return location;
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
