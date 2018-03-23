package sametileri.project.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sametileri.project.R;
import sametileri.project.objects.Register;
import sametileri.project.objects.Tarla;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Marker> markerList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        if (googleServicesAvailable()) {
            Toast.makeText(getApplicationContext(), "Connecting to google services!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_maps);
            markerList = new ArrayList<>();
            initMap();
        }

    }

    private Marker setMarker(double lat, double lng, String title, int tarlaId) {

        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .position(new LatLng(lat, lng))
                .title(title)
                .snippet(tarlaId + "");

        Marker marker = mMap.addMarker(options);
        markerList.add(marker);

        return marker;

    }

    public void geoLocate(View view) throws IOException {

        if (view.getId() == R.id.btnSaveSaha) {
            for (int i = 0; i < markerList.size(); i++) {
                if (markerList.get(i).getTag() != null) {
                    int a = Integer.parseInt(markerList.get(i).getSnippet());
                    RegisterActivity.fieldIds.add(a);
                }
            }
            Intent passMapsActv = new Intent(this, RegisterActivity.class);
            passMapsActv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(passMapsActv);
        }
        else if (view.getId() == R.id.btnChangeMapMode){
            Context wrapper = new ContextThemeWrapper(MapsActivity.this, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, view);
            popupMenu.getMenuInflater().inflate(R.menu.mapmenu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.mapTypeNormal:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case R.id.mapTypeTerrain:
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                        case R.id.mapTypeSatellite:
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case R.id.mapTypeHybrid:
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }


    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    public boolean googleServicesAvailable() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Can't connect to google services.", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {

            String SERVER_URL = "http://193.140.150.25:3542/AccountServices/AccountServices.asmx/GetLocations";
            String jsonString = MainActivity.postJson("", "", SERVER_URL);

            /*InputStream inputStream = getResources().openRawResource(R.raw.locations);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int ctr;
            try {
                ctr = inputStream.read();
                while (ctr != -1) {
                    byteArrayOutputStream.write(ctr);
                    ctr = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String jsonString = byteArrayOutputStream.toString();*/


            JSONObject mainJson = null;
            try {
                mainJson = new JSONObject(jsonString);
                JSONArray locations = mainJson.getJSONArray("Locations");

                for (int i = 0; i < locations.length(); i++) {
                    JSONObject index = locations.getJSONObject(i);

                    JSONObject area = index.getJSONObject("Area");

                    JSONArray coorListArea = area.getJSONArray("Coor_list");
                    PolylineOptions saha = new PolylineOptions();

                    for (int j = 0; j < coorListArea.length(); j++) {
                        JSONObject latlng = coorListArea.getJSONObject(j);
                        double lat = latlng.getDouble("Coor_x");
                        double lng = latlng.getDouble("Coor_y");
                        goToLocationZoom(lat, lng, 15);
                        saha.add(new LatLng(lat, lng));
                    }
                    mMap.addPolyline(saha);

                    PolylineOptions tarla = new PolylineOptions();

                    JSONArray fields = index.getJSONArray("Fields");
                    for (int j = 0; j < fields.length(); j++) {

                        JSONObject field = fields.getJSONObject(j);

                        int fieldId = field.getInt("Field_id");
                        String title = field.getString("Field_name");

                        JSONArray coorListTarla = field.getJSONArray("Coor_list");

                        for (int k = 0; k < coorListTarla.length(); k++) {

                            JSONObject latlng = coorListTarla.getJSONObject(k);
                            double lat = latlng.getDouble("Coor_x");
                            double lng = latlng.getDouble("Coor_y");

                            if (k != coorListTarla.length() - 1)
                                tarla.add(new LatLng(lat, lng));
                            else
                                setMarker(lat, lng, title, fieldId);

                        }
                        mMap.addPolyline(tarla);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    marker.showInfoWindow();

                    if (marker.getTag() != null && marker.getTag().equals("secildi")) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        marker.setTag(null);
                    } else {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        marker.setTag("secildi");
                    }

                    return true;
                }
            });


        }

    }

}