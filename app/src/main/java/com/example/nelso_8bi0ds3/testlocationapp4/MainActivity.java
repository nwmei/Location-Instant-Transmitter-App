package com.example.nelso_8bi0ds3.testlocationapp4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    TextView mLatitudeText;
    TextView mLongitudeText;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //request queue for volley library
        requestQueue = Volley.newRequestQueue(this);

        Button alert = findViewById(R.id.alert);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use Twilio to send text onClick
                String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mLatitudeText.getText() + ',' + mLongitudeText.getText() +"&";
                String api_key = "key=AIzaSyAddj-q2zGCJQnEtdfjbUu4VO4ZZsOdsKY";

                JsonObjectRequest request = new JsonObjectRequest(url + api_key, new
                        Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String address = response.getJSONArray("results").getJSONObject(0).getString
                                            ("formatted_address");
                                    mLatitudeText.setText(address);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(request);
                // use Twilio to send text onClick

                SMS sms = new SMS(); // creates a new SMS that takes the google API output and sends it to the input phone numbers
            }
        });
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Location mLastLocation = new Location("");

                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                    }
                    if (mLastLocation != null) {
                        mLatitudeText = findViewById(R.id.latitudeID);
                        mLongitudeText = findViewById(R.id.longitudeID);
                        //set the text views to the latitude and longitude values
                        mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                        mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class SMS {
        // Find your Account Sid and Token at twilio.com/user/account
        static final String ACCOUNT_SID = "AC679dc9808e33e85f3a7a2c990670e70c";
        static final String AUTH_TOKEN = "8821bfa14b21fe7a4eaae0739eaf58df";

        SMS(){
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message message = Message.creator(new PhoneNumber("+9148068050"),
                    new PhoneNumber("+2012988385"),"This is message").create();

            System.out.println(message.getSid());
        }
    }
}

