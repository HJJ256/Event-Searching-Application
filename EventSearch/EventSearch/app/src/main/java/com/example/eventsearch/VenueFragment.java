package com.example.eventsearch;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenueFragment} factory method to
 * create an instance of this fragment.
 */
public class VenueFragment extends Fragment implements OnMapReadyCallback {

    private TextView loading,noDetails;
    private TableLayout venueDetailTable;

    private String venueDetailString, latitude,longitude;
    private JsonObject venue;
    private Gson gson;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gson = new Gson();
        venueDetailString = getArguments().getString("venueDetails");
        venue = gson.fromJson(venueDetailString,JsonObject.class);

        venueDetailTable = view.findViewById(R.id.venueDetailTable);
        loading = view.findViewById(R.id.loadingVenue);
        noDetails = view.findViewById(R.id.noDetailsVenue);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        try {
            update(venue);
        } catch (JsonIOException | JSONException e) {
            Toast.makeText(getContext(), "JSON Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_venue, container, false);
    }
    public void update(JsonObject venue) throws JSONException {
        Log.i("AAAA",venue.toString());
        loading.setText("");
        String[] keys = {"Address","City","Phone","OpenHours","GeneralRule","ChildRule"};
        String[] headings = {"Address","City","Phone Number","Open Hours","General Rule","Child Rule"};
        JsonObject venueJson = venue.getAsJsonObject("nameValuePairs");
        JsonObject loc = gson.fromJson("{}",JsonObject.class);
        try {
            loc = venueJson.getAsJsonObject("Location").getAsJsonObject("nameValuePairs");
        }
        catch (Error error){
            //Toast.makeText(getContext(), "Exact Venue Location Not Found", Toast.LENGTH_SHORT).show();
        }
        if(!venueJson.toString().equals("{}")){
            noDetails.setVisibility(View.INVISIBLE);
            for (int i = 0; i < keys.length ; i++) {
                String heading = headings[i];
                String value = venueJson.get(keys[i]).toString();
                value = value.replaceAll("^\"+|\"+$", "");
                //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                if(!value.equalsIgnoreCase("N/A") && !value.equalsIgnoreCase("")){
                    TableRow tr = new TableRow(getActivity());
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TextView head = new TextView(getActivity());
                    head.setText(heading);
                    head.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    head.setPaddingRelative(0,0,80,0);
                    head.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tr.addView(head);
                    tr.setPaddingRelative(0,0,0,50);

                    TextView val = new TextView(getActivity());
                    val.setText(value);
                    val.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    tr.addView(val);

                    venueDetailTable.addView(tr,
                            new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
            if(!loc.toString().equals("{}")){
                latitude = loc.get("latitude").toString();
                latitude = latitude.replaceAll("^\"+|\"+$", "");
                longitude = loc.get("longitude").toString();
                longitude = longitude.replaceAll("^\"+|\"+$", "");
                mapFragment.getView().setVisibility(View.VISIBLE);
                mapFragment.getMapAsync(this);
            }
            else{
                mapFragment.getView().setVisibility(View.GONE);
            }
        }
        else {
            noDetails.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions()
                .position(location)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
    }
}