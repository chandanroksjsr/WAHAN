package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity implements LocationListener{

	private TextView txtName;
	private TextView txtEmail;
	private TextView loca;
	private Button btnLogout;
	private Button btntrack;
	private ProgressDialog pDialog;
GPSTracker gps;
	double tmplat=0;
	double tmplong=0;
	private SQLiteHandler db;
	private SessionManager session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.email);
		loca = (TextView) findViewById(R.id.loc);
		btntrack = (Button) findViewById(R.id.btntrack);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");

		// Displaying the user details on the screen
		txtName.setText(name);


		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		btntrack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this,
								map.class);
						startActivity(intent);
			}
		});

//		final Handler h = new Handler();
//		final int delay = 1000; //milliseconds
//
//		h.postDelayed(new Runnable() {
//			public void run() {
//
//
//
//
//				h.postDelayed(this, delay);
//			}
//		}, delay);
//
//
	}


	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void time() {


				gps = new GPSTracker(MainActivity.this);

				// check if GPS enabled
				if (gps.canGetLocation()) {

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

update(String.valueOf(latitude), String.valueOf(longitude));

					// \n is for new line
						} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}




			}



	private void update(final String email, final String password) {
		// Tag used to cancel the request
	String tag_string_req = "update_location";



		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_UPDATE, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				//Log.d(TAG, "Login Response: " + response.toString());
				hideDialog();

				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");

					// Check for error node in json
					if (!error) {
						txtEmail.setText(email +","+password);
						//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + email + "\nLong: " + password, Toast.LENGTH_LONG).show();

						// user successfully logged in
						// Create login session
					//	session.setLogin(true);

						// Now store the user in SQLite
//						String uid = jObj.getString("uid");
//
//						JSONObject user = jObj.getJSONObject("user");
//						String name = user.getString("name");
//						String email = user.getString("email");
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

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				HashMap<String, String> user = db.getUserDetails();

				String idi = user.get("uid");
				Map<String, String> params = new HashMap<String, String>();
				params.put("latitude", email);
				params.put("longitude", password);
				params.put("user", idi);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	@Override
	public void onLocationChanged(Location location) {
		time();
		Toast.makeText(getApplicationContext(),"hii"+location.getLatitude(),Toast.LENGTH_LONG).show();
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

