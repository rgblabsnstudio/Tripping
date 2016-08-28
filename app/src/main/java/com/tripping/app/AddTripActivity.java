package com.tripping.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.BreakIterator;

public class AddTripActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TextView.OnEditorActionListener, View.OnFocusChangeListener {
    private Button mSearchRoutes;
    private Button mClearForm;

    private TextView mStartingPnt;
    private LatLng mSourceLatLong;
    private TextView mDesitnationPnt;
    private LatLng mDestinationLatLong;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    BreakIterator mLongitudeText;
    BreakIterator mLatitudeText;

    private int PLACE_PICKER_REQUEST = 1;

    private Place place;

    private int textBoxToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mClearForm = (Button) findViewById(R.id.btn_clear);
        mSearchRoutes = (Button) findViewById(R.id.btn_srch_route);

        mStartingPnt = (EditText) findViewById(R.id.starting_pnt_txt);
        mDesitnationPnt = (EditText) findViewById(R.id.destination_pnt_txt);

        mClearForm.setOnClickListener(this);
        mSearchRoutes.setOnClickListener(this);

        //mStartingPnt.setOnEditorActionListener(this);
        //mDesitnationPnt.setOnEditorActionListener(this);
        mStartingPnt.setOnClickListener(this);
        mDesitnationPnt.setOnClickListener(this);
        //mStartingPnt.setOnFocusChangeListener(this);
        //mDesitnationPnt.setOnFocusChangeListener(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_srch_route:
                //startActivity(new Intent(this, TripRouteSelectActivity.class));

                break;
            case R.id.btn_clear:
                mDesitnationPnt.setText("");
                mStartingPnt.setText("");
                break;
            case R.id.starting_pnt_txt:
                /*Snackbar.make(v, "Replace with your own action on Starting Point.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
                textBoxToUse = R.id.starting_pnt_txt;
                getPlaceToSelect();
                break;
            case R.id.destination_pnt_txt:
               /* Snackbar.make(v, "Replace with your own action on Destination Point.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                textBoxToUse = R.id.destination_pnt_txt;
                getPlaceToSelect();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));

            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

       getPlaceToSelect();
        switch (v.getId()){
            case R.id.starting_pnt_txt:
               // if(hasFocus){
                    textBoxToUse = R.id.starting_pnt_txt;
                   // getPlaceToSelect();
                  //  v.findViewById(R.id.starting_pnt_txt).setFocusable(false);
                //}
                break;
            case R.id.destination_pnt_txt:
                //if(hasFocus){
                    textBoxToUse = R.id.destination_pnt_txt;
                    //getPlaceToSelect();
                  //  v.findViewById(R.id.destination_pnt_txt).setFocusable(false);
                //}
                break;
        }

    }
    public void getPlaceToSelect(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(intent, this);
            }
            switch (textBoxToUse){
                case R.id.starting_pnt_txt:
                    mStartingPnt.setText(place.getName());
                    mSourceLatLong = place.getLatLng();
                    break;
                case R.id.destination_pnt_txt:
                    mDesitnationPnt.setText(place.getName());
                    mDestinationLatLong = place.getLatLng();
                    break;
            }
        }

    }
}
