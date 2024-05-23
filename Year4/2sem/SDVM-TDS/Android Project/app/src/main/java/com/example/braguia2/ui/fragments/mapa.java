package com.example.braguia2.ui.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class mapa extends Fragment {

    private MapView mMapView;
    private List<Ponto> pontos;
    private double[] lats;
    private double[] lngs;

    public mapa() {
        // Required empty public constructor
    }
    public mapa(List<Ponto> pontos) {
        this.pontos = pontos;
    }

    public static mapa newInstance(String param1, String param2) {
        mapa fragment = new mapa();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {   
            this.lats = getArguments().getDoubleArray("latitude");
            this.lngs = getArguments().getDoubleArray("longitude");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_mapa, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Map Fragment Itself
                // Enable zoom controls
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                TrailsViewModel trailsViewModel = new ViewModelProvider(requireActivity()).get(TrailsViewModel.class);
                trailsViewModel.getAllPontos().observe(getViewLifecycleOwner(), points -> {
                    if (points != null && !points.isEmpty()) {
                        List<LatLng> latLngs = new ArrayList<>();
                        for (Ponto point : points) {
                            addMarkersToMap(googleMap, point);
                        }
                    }
                });


                // Google Maps Logistics
                List<String> waypoints = new ArrayList<>();

                if (lats != null && lngs != null){
                    for (int i=0;i<lats.length;i++) {
                        String coords = (lats[i]) + ", " + (lngs[i]);
                        waypoints.add(coords);
                    }
                    openGoogleMaps(googleMap, waypoints);
                }
                else {

                }
            }
        });

        return rootView;
    }

    private void addMarkersToMap(GoogleMap googleMap, Ponto point) {
            LatLng x = new LatLng(point.getPinLat(), point.getPinLng());
            googleMap.addMarker(new MarkerOptions().position(x));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Bundle args = new Bundle();
                    args.putInt("id", Integer.parseInt(point.getId()));

                    Navigation.findNavController(mMapView).navigate(R.id.action_mapa_fragment_to_ponto_fragment,args);

                    return true; // Return true to indicate that the click event has been consumed
                }
            });
    }

    private void openGoogleMaps(GoogleMap googleMap, List<String> waypoints) {
        // Create a StringBuilder to construct the Google Maps URL
        StringBuilder urlBuilder = new StringBuilder("http://maps.google.com/maps?daddr=");

        for (String waypoint : waypoints) {
            urlBuilder.append(waypoint).append("+to:");
        }

        // Remove the last "+to:" from the URL
        urlBuilder.setLength(urlBuilder.length() - 4);

        // Create an Intent to open the Google Maps app with the URI
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlBuilder.toString()));
        intent.setPackage("com.google.android.apps.maps");

        // Verify that the Google Maps app is installed before starting the activity
        PackageManager packageManager = getActivity().getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent);
        } else {
            // If Google Maps app is not installed, display a message or fallback to a web browser
            Toast.makeText(getActivity(), "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
