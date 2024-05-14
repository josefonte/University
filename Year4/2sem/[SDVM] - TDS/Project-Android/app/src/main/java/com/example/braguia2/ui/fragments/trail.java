package com.example.braguia2.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.model.User.User;
import com.example.braguia2.viewmodel.LocationService;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.example.braguia2.viewmodel.UserViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class trail extends Fragment {

    private int trail_id;
    private MapView mMapView;
    public trail(){
        super(R.layout.fragment_trail);
    } // Incoerencia com o outro lado


    public trail(int id){
        super(R.layout.fragment_trail);
        this.trail_id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.trail_id  = args.getInt("trailId", -1); // Replace -1 with default value if needed
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trail, container, false);

        TrailsViewModel trailsViewModel = new ViewModelProvider(requireActivity()).get(TrailsViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        trailsViewModel.getTrailById(trail_id).observe(getViewLifecycleOwner(), trail -> {
            LiveData<User> x;
            try {
                x = userViewModel.getUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (x != null) {
                x.observe(getViewLifecycleOwner(), user -> {
                    // Observe changes in the user data
                    if (user != null && trail != null) {
                        // Both user and trail data are available, call loadView
                        loadView(view, trail, user);
                    }
                });

            }
            else{
                loadView(view,trail,null);
            }
        });

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pontos_interesse_recycleview, new PontosTextoListFragment(trail_id));
        transaction.add(R.id.conteudo_top, new MediaListFragment(trail_id));
        //transaction.addToBackStack(null);
        transaction.commit();

        ImageButton returnButton = view.findViewById(R.id.returnn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_trail_fragment_to_explorar_fragment);
            }
        });

        mMapView = view.findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();



        return view;
    }

    private void loadView(View view, Trail trail, User user){
        List<Ponto> pontos = trail.getPontos();

        TextView titulo = view.findViewById(R.id.Trilhotitulo);
        titulo.setText(trail.getTrailName());

        //ImageView imagem = view.findViewById(R.id.img_trail);
        //Picasso.get()
        //        .load(trail.getUrl().replace("http:", "https:"))
        //        .into(imagem);

        TextView desc = view.findViewById(R.id.descricao);
        desc.setText(trail.getTrailDesc());

        TextView stat1 = view.findViewById(R.id.info1_cima);
        stat1.setText(Double.toString(trail.getTrailDistance()));

        TextView stat2 = view.findViewById(R.id.info2_cima);
        stat2.setText(Integer.toString(trail.getTrailDuration()));

        TextView stat3 = view.findViewById(R.id.info3_baixo);
        stat3.setText(Integer.toString((trail.getEdges().size()) + 1));

        TextView stat4 = view.findViewById(R.id.info4_baixo);
        stat4.setText(trail.getTrailDifficultyExtenso());


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                LatLng startPoint = new LatLng(pontos.get(0).getPinLat(), pontos.get(0).getPinLng());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 11));
                PolylineOptions polylineOptions = new PolylineOptions().color(Color.RED).width(5);

                for (Ponto p : pontos) {
                    LatLng point = new LatLng(p.getPinLat(), p.getPinLng());
                    polylineOptions.add(point);
                }
                googleMap.addPolyline(polylineOptions);
            }
        });


        ImageView startTrail = view.findViewById(R.id.imageButton2);
        startTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("USER",user.toString());
                if (user.getEmail() != null){
                    user.getHistorico_T().add(Integer.parseInt(trail.getId()));
                    Log.d("HISTORICO",Integer.toString(user.getHistorico_T().get(0)));
                }

                List<Double> lats = pontos.stream().map(Ponto::getPinLat).collect(Collectors.toList());
                List<Double> lngs = pontos.stream().map(Ponto::getPinLng).collect(Collectors.toList());
                Bundle args = new Bundle();
                args.putDoubleArray("latitude", lats.stream().mapToDouble(Double::doubleValue).toArray());
                args.putDoubleArray("longitude", lngs.stream().mapToDouble(Double::doubleValue).toArray());

                Navigation.findNavController(v).navigate(R.id.action_trail_fragment_to_mapa_fragment, args);
            }
        });
    }


    private void sendCommandToService(String action){
        Intent intent = new Intent(requireContext(), LocationService.class);
        intent.setAction(action);
        requireContext().startService(intent);
    }
}











































