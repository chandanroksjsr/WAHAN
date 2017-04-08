package info.androidhive.loginandregistration.activity;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        final int n = 0;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        Marker marker;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
//            // here to request the missing permissions, and then overriding
//             // public void onRequestPermissionsResult(int requestCode, String[] ACCES, int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
        mMap.setMyLocationEnabled(true);

        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

		h.postDelayed(new Runnable() {
			public void run() {
        //do something

            update();

				h.postDelayed(this, delay);
			}
		}, delay);

//        if(marker!=null){
//            marker.remove();
//        }

//        LatLng mLatLng = new LatLng(lon, lat);
//
//        marker = mMap.addMarker(new MarkerOptions().position(mLatLng).title("My Title").snippet("My Snippet").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));

    }




    private void time() {

        // check if GPS enabled





    }



    private void update() {
        // Tag used to cancel the request
        String tag_string_req = "new_loc";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + email + "\nLong: " + password, Toast.LENGTH_LONG).show();

                        // user successfully logged in
                        // Create login session
                        //	session.setLogin(true);

                        // Now store the user in SQLite
                        //String uid = jObj.getString("user");
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);



                        JSONArray users = jObj.getJSONArray("location");

                        for (int i=0; i<users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);

                            String lat = user.getString("lat");
                            String lon = user.getString("lon");
                            String uid = user.getString("uid");
                            LatLng sydney = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                            Marker marker;
                            marker = mMap.addMarker(new MarkerOptions().position(sydney).title(uid).icon(icon));
                            animateMarker(marker,sydney,false);

                        }



//						String created_at = user
//								.getString("created_at");

                        // Inserting row in users table


                        // Launch main activity
//						Intent intent = new Intent(MainActivity.this,
//								MainActivity.class);
//						startActivity(intent);
//						finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {



        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
        AppController.getInstance().cancelPendingRequests(strReq);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                double n=0;
                double lng =  toPosition.longitude;
                double lat =  toPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));







            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        update();
        
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
