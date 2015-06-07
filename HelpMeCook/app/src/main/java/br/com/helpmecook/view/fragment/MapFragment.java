package br.com.helpmecook.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.security.Provider;
import java.util.ArrayList;

import br.com.helpmecook.R;
import br.com.helpmecook.model.GooglePlace;
import br.com.helpmecook.view.activity.MainActivity;

/**
 * Created by Thais on 26/05/2015.
 */
public class MapFragment extends Fragment {
    private GoogleMap googleMap;

    ArrayList<GooglePlace> places;

    final String GOOGLE_KEY = "AIzaSyBeGSqRCSxFPDKDnMy8f8w4pdMawYpvluo";
    final String LAT = "40.7463956";
    final String LON = "-73.9852992";

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_map,
                container, false);

//        setUpMapIfNeeded();

        new GooglePlaceTask().execute();

        return fragmentView;
    }
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (googleMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//            // Check if we were successful in obtaining the map.
//            if (googleMap != null) {
//                setUpMap();
//            }
//        }
//    }

    private void setUpMap(){
        googleMap.setMyLocationEnabled(true);


        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location myLocation = locationManager.getLastKnownLocation(provider);

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("tu"));

    }

    public class GooglePlaceTask extends AsyncTask {
        String temp;

        @Override
        protected Object doInBackground(Object[] params) {
            temp = fazerChamada("https://maps.googleapis.com/maps/api/place/search/json?location=" + LAT + "," + LON + "&radius=100&sensor=true&key=" + GOOGLE_KEY +
                    "&types=restaurant&types=grocery_or_supermarket");
            System.out.println("Acessando: " + "https://maps.googleapis.com/maps/api/place/search/json?location=" + LAT + "," + LON + "&radius=100&sensor=true&key=" + GOOGLE_KEY);
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
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                System.out.println("JSON " + replyString.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(replyString);
            return replyString.trim();
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
