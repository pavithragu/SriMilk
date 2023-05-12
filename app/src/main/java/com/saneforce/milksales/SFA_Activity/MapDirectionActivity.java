package com.saneforce.milksales.SFA_Activity;

import static com.saneforce.milksales.SFA_Activity.Nearby_Outlets.shared_common_pref;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.saneforce.milksales.Activity_Hap.AddNewRetailer;
import com.saneforce.milksales.Activity_Hap.Checkin;
import com.saneforce.milksales.Activity_Hap.Dashboard;
import com.saneforce.milksales.Activity_Hap.Dashboard_Two;
import com.saneforce.milksales.Activity_Hap.ImageCapture;
import com.saneforce.milksales.Activity_Hap.SFA_Activity;
import com.saneforce.milksales.Common_Class.Common_Class;
import com.saneforce.milksales.Common_Class.Constants;
import com.saneforce.milksales.Common_Class.Shared_Common_Pref;
import com.saneforce.milksales.Interface.LocationEvents;
import com.saneforce.milksales.R;
import com.saneforce.milksales.common.LocationFinder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapDirectionActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final int REQUEST_CODE = 101;
    static String googlePlacesData;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap mGoogleMap;
    TextView AddressTextview, ReachedOutlet;
    ImageView imag_back;
    Common_Class common_class;
    String sb;
    private Polyline mPolyline;
    Marker currentLocationMarker;
    public static String TAG = "MapDirectionActivity";
    private String status = "", CheckInfo = "CheckInDetail", UserInfo = "MyPrefs";
    SharedPreferences UserDetails, CheckInDetails;
    com.saneforce.milksales.Activity_Hap.Common_Class DT = new com.saneforce.milksales.Activity_Hap.Common_Class();
    private double radius = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map_direction);
            // geofencingClient = LocationServices.getGeofencingClient(this);
            status = getIntent().getStringExtra(Constants.NEW_OUTLET);
            status = status == null ? "" : status;

            common_class = new Common_Class(this);
            CheckInDetails = getSharedPreferences(CheckInfo, Context.MODE_PRIVATE);
            UserDetails = getSharedPreferences(UserInfo, Context.MODE_PRIVATE);

            AddressTextview = findViewById(R.id.AddressTextview);
            ReachedOutlet = findViewById(R.id.tvStartDirection);
            imag_back = findViewById(R.id.imag_back);
            imag_back.setOnClickListener(this);
            ReachedOutlet.setOnClickListener(this);

            if (status.equalsIgnoreCase("GEO"))
                ReachedOutlet.setText("Get Direction");
//                ReachedOutlet.setVisibility(View.GONE);


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // getDirection();
            ImageView ivToolbarHome = findViewById(R.id.toolbar_home);
            ivToolbarHome.setOnClickListener(this);
            //  common_class.gotoHomeScreen(this, ivToolbarHome);

        } catch (Exception e) {
            Log.v(TAG + "onCreate:", e.getMessage());
        }

    }


    public void openHome() {
        Boolean CheckIn = CheckInDetails.getBoolean("CheckIn", false);

        if (CheckIn == true) {
            if (status.equalsIgnoreCase("GEO")) {
                Intent Dashboard = new Intent(MapDirectionActivity.this, Dashboard_Two.class);
                Dashboard.putExtra("Mode", "CIN");
                startActivity(Dashboard);
            } else {
                common_class.CommonIntentwithoutFinish(SFA_Activity.class);

            }
        } else {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        new LocationFinder(getApplication(), new LocationEvents() {
            @Override
            public void OnLocationRecived(Location location) {
                try {
                    // clocation=location;
                    currentLocation = location;
                    Shared_Common_Pref.Outletlat = currentLocation.getLatitude();
                    Shared_Common_Pref.Outletlong = currentLocation.getLongitude();
                    fetchLocation();
                    DownloadTask downloadTask = new DownloadTask();
                    // Start downloading json data from Google Directions API
                    //downloadTask.execute(getIntent().getStringExtra(Constants.MAP_ROUTE));
                    String url = common_class.getDirectionsUrl(getIntent().getStringExtra(Constants.DEST_LAT) + "," +
                            getIntent().getStringExtra(Constants.DEST_LNG), MapDirectionActivity.this);

                    downloadTask.execute(url);
                } catch (Exception e) {
                    Log.v(TAG, e.getMessage());
                }


            }
        });

    }


    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    if (location != null) {
                        currentLocation = location;
                        Shared_Common_Pref.Outletlat = currentLocation.getLatitude();
                        Shared_Common_Pref.Outletlong = currentLocation.getLongitude();
                        Shared_Common_Pref.OutletAddress = getCompleteAddressString(currentLocation.getLatitude(), currentLocation.getLongitude());
                        // Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                        if (status.equalsIgnoreCase("GEO"))
                            AddressTextview.setText("" + getCompleteAddressString(Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LAT))
                                    , Double.valueOf(getIntent().getStringExtra(Constants.DEST_LNG))));

                        else
                            AddressTextview.setText("" + getCompleteAddressString(currentLocation.getLatitude(), currentLocation.getLongitude()));

                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
                        assert supportMapFragment != null;
                        supportMapFragment.getMapAsync(MapDirectionActivity.this);
                    }
                } catch (Exception e) {
                }
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mGoogleMap = googleMap;
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            //  Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(getCompleteAddressString(currentLocation.getLatitude(), currentLocation.getLongitude())));

            Double laty = Shared_Common_Pref.Outletlat;
            Double lngy = Shared_Common_Pref.Outletlong;
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
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(laty, lngy), 15));
            if (status.equalsIgnoreCase("GEO"))
                AddressTextview.setText("" + getCompleteAddressString(Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LAT))
                        , Double.valueOf(getIntent().getStringExtra(Constants.DEST_LNG))));

            else
                AddressTextview.setText("" + getCompleteAddressString(currentLocation.getLatitude(), currentLocation.getLongitude()));


            if (currentLocationMarker != null)
                currentLocationMarker.remove();

            if (!status.equalsIgnoreCase("GEO"))
                currentLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(("your location")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            distance();
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }


        //new
        //  mMap = googleMap;

//        LatLng barcelona = new LatLng(41.385064,2.173403);
//        mGoogleMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));
//
//        LatLng madrid = new LatLng(40.416775,-3.70379);
//        mGoogleMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
//
//        LatLng zaragoza = new LatLng(41.648823,-0.889085);
//
//        //Define list to get all latlng for the route
//        List<LatLng> path = new ArrayList();
//
//
//        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey(R.string.map_api_key)
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
//        try {
//            DirectionsResult res = req.await();
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                DirectionsRoute route = res.routes[0];
//
//                if (route.legs !=null) {
//                    for(int i=0; i<route.legs.length; i++) {
//                        DirectionsLeg leg = route.legs[i];
//                        if (leg.steps != null) {
//                            for (int j=0; j<leg.steps.length;j++){
//                                DirectionsStep step = leg.steps[j];
//                                if (step.steps != null && step.steps.length >0) {
//                                    for (int k=0; k<step.steps.length;k++){
//                                        DirectionsStep step1 = step.steps[k];
//                                        EncodedPolyline points1 = step1.polyline;
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                            for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                path.add(new LatLng(coord1.lat, coord1.lng));
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch(Exception ex) {
//            Log.e(TAG, ex.getLocalizedMessage());
//        }
//
//        //Draw the polyline
//        if (path.size() > 0) {
//            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//            mMap.addPolyline(opts);
//        }
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));

        //new
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                // Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.toolbar_home:
                    openHome();
                case R.id.imag_back:
                    finish();
                    break;
                case R.id.tvStartDirection:
                    if (ReachedOutlet.getText().toString().contains("Check-In")) {

                        Log.v("distance", ":" + distance() + ":Radius:" + radius);

                        if ((radius > 0 && distance() <= radius) || distance() < 200) {
                            String ETime = CheckInDetails.getString("CINEnd", "");
                            if (!ETime.equalsIgnoreCase("")) {
                                String CutOFFDt = CheckInDetails.getString("ShiftCutOff", "0");
                                String SftId = CheckInDetails.getString("Shift_Selected_Id", "0");
                                if (DT.GetCurrDateTime(this).getTime() >= DT.getDate(CutOFFDt).getTime() || SftId == "0") {
                                    ETime = "";
                                }
                            }
                            if (!ETime.equalsIgnoreCase("")) {
                                Intent takePhoto = new Intent(this, ImageCapture.class);
                                takePhoto.putExtra("Mode", "CIN");
                                takePhoto.putExtra("ShiftId", CheckInDetails.getString("Shift_Selected_Id", ""));
                                takePhoto.putExtra("ShiftName", CheckInDetails.getString("Shift_Name", ""));
                                takePhoto.putExtra("On_Duty_Flag", CheckInDetails.getString("On_Duty_Flag", "0"));
                                takePhoto.putExtra("ShiftStart", CheckInDetails.getString("ShiftStart", "0"));
                                takePhoto.putExtra("ShiftEnd", CheckInDetails.getString("ShiftEnd", "0"));
                                takePhoto.putExtra("ShiftCutOff", CheckInDetails.getString("ShiftCutOff", "0"));
                                startActivity(takePhoto);
                            } else {
                                Intent i = new Intent(this, Checkin.class);
                                startActivity(i);
                            }
                        } else {
                            common_class.showMsg(this, "Please Check-In your nearby HO Location");
                        }
                    } else if (ReachedOutlet.getText().toString().toLowerCase(Locale.ROOT).contains("get direction")) {
                        try {
                            shared_common_pref.save(Constants.DEST_NAME, getIntent().getStringExtra(Constants.DEST_NAME));
                        } catch (Exception e) {
                        }
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + getIntent().getStringExtra(Constants.DEST_LAT) + "," + getIntent().getStringExtra(Constants.DEST_LNG) + "&mode=l");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivityForResult(mapIntent, 1000);
                    } else {
                        sb = shared_common_pref.getvalue(Constants.PLACE_ID_URL);
                        if (common_class.isNetworkAvailable(this))
                            new findPlaceDetail().execute();
                    }
                    break;

            }
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }
    }

    private void drawCircle(LatLng point) {

        String val = UserDetails.getString("radius", "");
        if (!Common_Class.isNullOrEmpty(val) && !val.equalsIgnoreCase("null"))
            radius = Double.parseDouble(val) * 1000;

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();
        // Specifying the center of the circle
        circleOptions.center(point);
        // Radius of the circle
        circleOptions.radius(radius <= 0 ? 200 : radius);
        // Border color of the circle
        circleOptions.strokeColor(Color.RED);
        // Fill color of the circle
        circleOptions.fillColor(0x1AFF0000);
        // Border width of the circle
        circleOptions.strokeWidth(1);
        // Adding the circle to the GoogleMap
        mGoogleMap.addCircle(circleOptions);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            fetchLocation();
        }
    }


    double distance() {
        Location startPoint = new Location("point A");
        startPoint.setLatitude(currentLocation.getLatitude());
        startPoint.setLongitude(currentLocation.getLongitude());

        Location endPoint = new Location("point B");
        endPoint.setLatitude(Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LAT)));
        endPoint.setLongitude(Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LNG)));
        double distance = startPoint.distanceTo(endPoint);

        if (status != null && status.equalsIgnoreCase("checkin")) {
            ReachedOutlet.setText("Check-In ");
            drawCircle(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()));

        } else if (distance > 200 || (status != null && status.equalsIgnoreCase("new") )|| status.toLowerCase(Locale.ROOT).contains("geo")) {
            ReachedOutlet.setText("Get Direction");
        } else {
            ReachedOutlet.setText("Create Outlet");
        }

        return distance;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public class DownloadUrl {

        public String readUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data);
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

    }

    class findPlaceDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googlePlacesData = downloadUrl.readUrl(sb);
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e(TAG + " doInBackground: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.v("get_dr_detttt", googlePlacesData + " :api: " + sb);

            Intent intent = new Intent(getApplicationContext(), AddNewRetailer.class);
            Shared_Common_Pref.Outler_AddFlag = "1";
            intent.putExtra(Constants.PLACE_ID, googlePlacesData);
            startActivity(intent);

        }
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                        LatLng latLng = new LatLng(lat, lng);
                    }

                    // Adding all the points in the route to LineOptions

                    if (!status.equalsIgnoreCase("GEO")) {
                        lineOptions.addAll(points);
                        lineOptions.width(8);
                        lineOptions.color(getResources().getColor(R.color.colorPrimaryDark));

                    }
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    if (mPolyline != null) {
                        mPolyline.remove();
                    }
                    mPolyline = mGoogleMap.addPolyline(lineOptions);

                    LatLng currentLatLng = new LatLng(Shared_Common_Pref.Outletlat, Shared_Common_Pref.Outletlong);

                    LatLng latLng = new LatLng(Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LAT)), Double.parseDouble(getIntent().getStringExtra(Constants.DEST_LNG)));
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                            .title(getIntent().getStringExtra(Constants.DEST_NAME)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                    if (distance() > 200) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(currentLatLng);
                        builder.include(latLng);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }

                } else
                    Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }

    }
}