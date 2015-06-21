package br.com.helpmecook.view.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import br.com.helpmecook.R;
import br.com.helpmecook.model.GooglePlace;

/**
 * Created by Kandarpa on 16/06/2015.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected static final String TAG = "basic-location-sample";
    private static final String GOOGLE_API_KEY = "AIzaSyDxHamWj6WwoF9lA8yPUsyxl5I9zwChzKY";
    private int PROXIMITY_RADIUS = 2000;
    ArrayList<GooglePlace> placesList;
    private ProgressDialog pDialog;
    private Context context;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private GoogleMap googleMap;
    MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        //Showing Current Location
        googleMap.setMyLocationEnabled(true);
        //My Location Button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        context = getActivity();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(getActivity(), "mLastLocation NAO e null", Toast.LENGTH_LONG).show();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            new googleplaces().execute();

        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_location_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(getActivity(), "mLastLocation NAO e null", Toast.LENGTH_LONG).show();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_location_found), Toast.LENGTH_LONG).show();
        }
    }
    //-----------------------------------------
    private class googleplaces extends AsyncTask<View, Void, String> {

        String placesJson;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if (context != null){
                pDialog = new ProgressDialog(context);
                pDialog.setMessage(getString(R.string.searching_places));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }*/
        }

        @Override
        protected String doInBackground(View... urls) {
            // make Call to the url
            placesJson = makeCall("https://maps.googleapis.com/maps/api/place/search/json?location="
                    + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude()
                    + "&radius="+ PROXIMITY_RADIUS +"&keyword=food&sensor=true&key=" + GOOGLE_API_KEY);
            Log.i("Places URL", "https://maps.googleapis.com/maps/api/place/search/json?location="
                    + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude()
                    + "&radius="+ PROXIMITY_RADIUS +"&keyword=food&sensor=true&key=" + GOOGLE_API_KEY);

            //print the call in the console
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (placesJson == null) {
                // we have an error to the call
                // we can also stop the progress bar
            } else {
                placesList = (ArrayList<GooglePlace>) parseGoogleParse(placesJson);
                plotPlaces();
            }
            // pDialog.dismiss();
        }
    }

    private void plotPlaces(){
        for (int i = 0; i < placesList.size(); i++) {

            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(placesList.get(i).getLat(), placesList.get(i).getLng())).title(placesList.get(i).getName());
            Log.i("PLACES", "category: " + placesList.get(i).getCategory());

            // Changing marker icon
            if (placesList.get(i).idType("cafe")){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.cafe));
            }else if (placesList.get(i).idType("restaurant")){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
            } else if (placesList.get(i).idType("supermarket")){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.supermarket));
            }else {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.other));
            }
            //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            // adding marker
            googleMap.addMarker(marker);
        }
    }

    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }

    private static ArrayList<GooglePlace> parseGoogleParse(final String response) {

        ArrayList<GooglePlace> temp = new ArrayList<GooglePlace>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("results")) {

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    GooglePlace poi = new GooglePlace();
                    if (jsonArray.getJSONObject(i).has("name")) {
                        poi.setName(jsonArray.getJSONObject(i).optString("name"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        poi.setLat(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lat"));
                        poi.setLng(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lng"));

                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                    poi.setOpenNow("YES");
                                } else {
                                    poi.setOpenNow("NO");
                                }
                            }
                        } else {
                            poi.setOpenNow("Not Known");
                        }
                        if (jsonArray.getJSONObject(i).has("types")) {
                            JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");

                            for (int j = 0; j < typesArray.length(); j++) {
                                poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                            }
                        }
                    }
                    temp.add(poi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<GooglePlace>();
        }
        return temp;
    }
}