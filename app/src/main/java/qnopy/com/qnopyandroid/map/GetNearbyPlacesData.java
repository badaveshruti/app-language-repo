package qnopy.com.qnopyandroid.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import qnopy.com.qnopyandroid.R;

/**
 * Created by Yogendra on 02-Feb-17.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    LatLng mCurrentLocation;
    String url;
    ProgressDialog progress;
    Context mContext;

    public GetNearbyPlacesData(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress = new ProgressDialog(mContext);
        progress.setMessage("Please wait...");
        progress.setIndeterminate(true);
        progress.setCancelable(true);
        progress.show();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            mCurrentLocation = (LatLng) params[2];

            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();

        if (result != null) {
            nearbyPlacesList = dataParser.parse(result);
            ShowNearbyPlaces(nearbyPlacesList);
        } else {
            Log.d("GooglePlacesReadTask", "No Hospitals Found");

        }

        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        progress.dismiss();

    }


    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        LatLng latLng, zoom_to_marker = null;
        double lat, lng, highest_rating = 0;

        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            String lati = googlePlace.get("lat"), longi = googlePlace.get("lng");

            if ((lati != null && !lati.equals("0")) && (longi != null && !longi.equals("0"))) {
                lat = Double.parseDouble(lati);
                lng = Double.parseDouble(longi);

                double temprating = Double.parseDouble(googlePlace.get("rating"));
                if (highest_rating < temprating) {
                    highest_rating = temprating;
                    zoom_to_marker = new LatLng(lat, lng);
                }
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);

                markerOptions.title(" (" + temprating + ")" + placeName + " : " + vicinity);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_nearby_hospital);
                markerOptions.icon(icon);
                mMap.addMarker(markerOptions);

            }

            //move map camera


        }
            // TODO: 11-Feb-17 focus on highest rating
//        if (zoom_to_marker != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(zoom_to_marker));
//            Log.i("GetNearByPlacesData", "Highest Rating Hospital:" + highest_rating);
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
//        }

        // TODO: 11-Feb-17 focus on current location
        zoom_to_marker = mCurrentLocation;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zoom_to_marker));
        Log.i("GetNearByPlacesData", "Nearest Hospital:" + highest_rating);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


    }
}