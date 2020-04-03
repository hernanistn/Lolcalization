package com.example.hernanidev.lolcalization;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private double lat;
    private double log;

    private GoogleMap mMap;
    private TextView txtAndress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new GetLatLong().execute();
        //new GetAndress().execute();
        Toast.makeText(getApplicationContext(), Double.toString(getLat())+","+Double.toString(getLat()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng localizacaoMotoboy = new LatLng(getLat(), getLog());
        mMap.addMarker(new MarkerOptions().position(localizacaoMotoboy).title("Localização do MotoBoy"));
        float zoomLevel = 16.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacaoMotoboy, zoomLevel));
        txtAndress = (TextView) findViewById(R.id.txtEndereço);

    }


    private class GetAndress extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) { //responsavel em mandar a url com o long e lat e receber da api as informações de cidade, bairro, e rua. etc.
            try {
                HttpDataHandler http = new HttpDataHandler();
                String response;
               // String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.0f,%.0f&key=AIzaSyDH4Wacsj7czu2fYzA8NpiXSVSatLeyFIU", getLat(), getLog());
                response = http.getHTTPData("https://maps.googleapis.com/maps/api/geocode/json?latlng="+getLat()+""+","+getLog()+""+"&key=AIzaSyDH4Wacsj7czu2fYzA8NpiXSVSatLeyFIU");
                return response;
            }catch (Exception ex){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) { //responsavel em tratar as informações e jogar para o TextView as informações em String.
            try {
                JSONObject jsonObject = new JSONObject(s);
                String andress = ((JSONArray) jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString(); //irá pegar a variavel do JSON "Formatted_Andress"

                txtAndress.setText(andress+" "+getLat()+" , "+getLog());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private class GetLatLong extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                HttpDataHandler http = new HttpDataHandler();
                String response;
                // String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.0f,%.0f&key=AIzaSyDH4Wacsj7czu2fYzA8NpiXSVSatLeyFIU", getLat(), getLog());
                response = http.getHTTPData("http://jm.pro.br/gps/projeto.php");
                return response;
            }catch (Exception ex){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject;
                JSONArray array = new JSONArray(s);
                jsonObject = array.getJSONObject(0);
                String lat[] = (jsonObject.getString("latitude").split(","));
                String lat = (jsonObject.getString("latitude").toString());
                String lon = (jsonObject.getString("longitude").toString());
                setLat(Double.parseDouble(lat));
                setLog(Double.parseDouble(lon));
           } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }







    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

}
