package br.com.helpmecook.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.Provider;
import java.util.ArrayList;

import br.com.helpmecook.R;
import br.com.helpmecook.model.GooglePlace;
import br.com.helpmecook.view.activity.MainActivity;

/**
 * Created by Thais on 26/05/2015.
 */
public class MapFragment2 extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ArrayList<GooglePlace> places;

    final String GOOGLE_KEY = "AIzaSyBeGSqRCSxFPDKDnMy8f8w4pdMawYpvluo";
    public String LAT = "0";
    public String LON = "0";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapFragment2.class.getSimpleName();
    private LocationRequest mLocationRequest;

    public MapFragment2() {
    }

//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_map,
                container, false);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(fragmentView.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds



        return fragmentView;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }
//    private void setUpMap(){
//        mMap.setMyLocationEnabled(true);
//
//
//        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        Criteria criteria = new Criteria();
//
//        String provider = locationManager.getBestProvider(criteria, true);
//        double latitude;
//        double longitude;
//
//        Location myLocation = locationManager.getLastKnownLocation(provider);
//
//
//        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//
//
//        if(myLocation != null){
//            latitude = myLocation.getLatitude();
//            longitude = myLocation.getLongitude();
//        }else{
//            Location   getLastLocation = locationManager.getLastKnownLocation
//                    (LocationManager.PASSIVE_PROVIDER);
//            longitude = getLastLocation.getLongitude();
//            latitude = getLastLocation.getLatitude();
//        }
//
//        LatLng latLng = new LatLng(latitude, longitude);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("tu"));
//    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LAT = String.valueOf(currentLatitude);
        LON = String.valueOf(currentLongitude);

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");

        mMap.addMarker(options);
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom =CameraUpdateFactory.zoomTo(12);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        new GooglePlaceTask().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }


























    public class GooglePlaceTask extends AsyncTask {
        String temp;

        @Override
        protected Object doInBackground(Object[] params) {
            temp = fazerChamada("https://maps.googleapis.com/maps/api/place/search/json?location=" + LAT + "," + LON + "&radius=500&sensor=true&key=" + GOOGLE_KEY +
                    "&types=grocery_or_supermarket&types=restaurant");
            //System.out.println("Acessando: " + "https://maps.googleapis.com/maps/api/place/search/json?location=" + LAT + "," + LON + "&radius=100&sensor=true&key=" + GOOGLE_KEY);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (temp == null) {
                // Chamada deu errado. Cota atingida? Conex�o perdida?
            }
            places = parseGoogleParse(temp);

            for (GooglePlace gp : places){
                Log.i("Place", "Nome: " + gp.getName());
                Log.i("Place", "Lat: " + gp.getLat());
                Log.i("Place", "Lon: " + gp.getLon());
                mMap.addMarker(new MarkerOptions().position(new LatLng(gp.getLat(), gp.getLon())).title(gp.getName()));
            }
        }

        public String fazerChamada(String url) {
            StringBuffer buffer_string = new StringBuffer(url);
            String replyString = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(buffer_string.toString());
            try {
                HttpResponse response = httpclient.execute(httpget);
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(20);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                // Resultado: string do JSON!
                replyString = new String(baf.toByteArray());
//                System.out.println("JSON " + replyString.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println(replyString);
            return replyString.trim();
        }

        private ArrayList parseGoogleParse(final String response) {

            ArrayList temp = new ArrayList();
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
                            poi.setLat(jsonArray.getJSONObject(i)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat"));

                            poi.setLon(jsonArray.getJSONObject(i)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng"));

                            if (jsonArray.getJSONObject(i).has("opening_hours")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                        poi.setOpen("YES");
                                    } else {
                                        poi.setOpen("NO");
                                    }
                                }
                            } else {
                                poi.setOpen("Not Known");
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
                return new ArrayList();
            }
            return temp;

        }
    }

}
