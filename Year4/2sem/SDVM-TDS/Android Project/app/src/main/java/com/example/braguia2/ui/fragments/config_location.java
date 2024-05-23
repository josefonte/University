
package com.example.braguia2.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.braguia2.R;

public class config_location extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;

    public config_location() {
        // Required empty public constructor
    }


    public static config_location newInstance(String param1, String param2) {
        config_location fragment = new config_location();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_config_location, container, false);

        handleLocation(view);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        ImageView btn_back = view.findViewById(R.id.go_back);

        btn_back.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_config_location_to_configs);
        });

    }

    public void handleLocation(View view){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        RadioButton enableLocationRadioButton = view.findViewById(R.id.radioButton1);
        RadioButton disableLocationRadioButton = view.findViewById(R.id.radioButton0);

        enableLocationRadioButton.setChecked(isGPSEnabled);
        disableLocationRadioButton.setChecked(!isGPSEnabled);

        enableLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLocation();
            }
        });

        disableLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLocation();
            }
        });
    }

    private void enableLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(locationSettingsIntent);
        }
    }

    private void disableLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(locationSettingsIntent);
        }
    }
}
