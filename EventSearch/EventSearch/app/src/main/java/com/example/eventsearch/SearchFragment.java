package com.example.eventsearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.io.Console;

public class SearchFragment extends Fragment {

    private EditText keyword,distance,location;
    private Spinner category,unit;
    private RadioGroup locationRadioGroup;
    private RadioButton here,customLocation; //Not Used
    private Button search,clear;
    private String[] segmentIds = {"","KZFzniwnSyZfZ7v7nJ","KZFzniwnSyZfZ7v7nE","KZFzniwnSyZfZ7v7na","KZFzniwnSyZfZ7v7nn","KZFzniwnSyZfZ7v7n1"};
    private String[] units = {"miles","km"};
    private String latlong = "";

    private FusedLocationProviderClient fusedLocationProviderClient;

    public SearchFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        getLocation();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ALL TECHNICAL AND LOGICAL STUFF
        keyword = getView().findViewById(R.id.keyword); //Variable Binding
        distance = getView().findViewById(R.id.distance);
        location = getView().findViewById(R.id.location);
        category = getView().findViewById(R.id.category);
        unit = getView().findViewById(R.id.unit);
        locationRadioGroup = getView().findViewById(R.id.locationRadioGroup);
        search = getView().findViewById(R.id.search);
        clear = getView().findViewById(R.id.clear);

        //fusedLocationProviderClient initialized
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.here){
                    location.setError(null);
                    location.setEnabled(false);
                }
                else{
                    location.setEnabled(true);
                }
            }
        });

        //Populating Category Spinner
        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(getContext(),R.array.categories, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoriesAdapter);

        //Populating Unit Spinner
        ArrayAdapter<CharSequence> unitsAdapter = ArrayAdapter.createFromResource(getContext(),R.array.units, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit.setAdapter(unitsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Toast.makeText(getContext(),
                        keyword.getText().toString()+" "+distance.getText().toString()+" "+location.getText().toString()+" "+units[unit.getSelectedItemPosition()],
                        Toast.LENGTH_SHORT).show();*/

                boolean validationSuccess = fieldValidation();
                if(validationSuccess){
                    //Toast.makeText(getContext(), "Validation Success", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(getContext(),SearchResults.class);
                    bundle.putString("keyword",keyword.getText().toString());
                    if(location.isEnabled()){
                        bundle.putString("location",location.getText().toString());
                    }
                    else{
                        bundle.putString("latlong",latlong);
                    }

                    bundle.putString("distance",distance.getText().toString());
                    bundle.putString("category",segmentIds[category.getSelectedItemPosition()]);
                    bundle.putString("unit",units[unit.getSelectedItemPosition()]);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    //Toast.makeText(getContext(), "Validation Failed", Toast.LENGTH_SHORT).show();
                    //Commented because I am showing Tooltips as in reference video
                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword.setText("");
                keyword.setError(null);
                location.setText("");
                location.setError(null);
                category.setSelection(0);
                unit.setSelection(0);
                distance.setText("10");
                locationRadioGroup.check(R.id.here);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        keyword.setText("");
        keyword.setError(null);
        location.setText("");
        location.setError(null);
        category.setSelection(0);
        unit.setSelection(0);
        distance.setText("10");
        locationRadioGroup.check(R.id.here);
    }

    private boolean fieldValidation(){
        boolean keyword_fail = false, location_fail=false;

        if (keyword.getText().toString().trim().equals("")){
            keyword.setError("Please enter mandatory field");
            keyword_fail=true;
        }
        if (location.isEnabled() && location.getText().toString().trim().equals("")){
            location.setError("Please enter mandatory field");
            location_fail=true;
        }
        if(!keyword_fail && !location_fail){
            return true;
        }
        else{
            return false;
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation(){
        if(checkPermissions()){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        latlong = latitude +","+ longitude;
                        //Toast.makeText(getContext(), latlong, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Location Permissions are not granted", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        return getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //DO ALL GRAPHICAL STUFF HERE

        return (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
    }
}