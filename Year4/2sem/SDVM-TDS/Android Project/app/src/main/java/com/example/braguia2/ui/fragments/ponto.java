package com.example.braguia2.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.braguia2.model.User.User;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.example.braguia2.viewmodel.UserViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ponto extends Fragment {

    private int id;
    private MapView mMapView;
    public ponto(){
        super(R.layout.fragment_ponto_interesse);
    }


    public ponto(int id){
        super(R.layout.fragment_ponto_interesse);
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.id = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ponto_interesse, container, false);

        TrailsViewModel trailsViewModel = new ViewModelProvider(requireActivity()).get(TrailsViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        trailsViewModel.getPontoById(id).observe(getViewLifecycleOwner(), ponto -> {
            LiveData<User> x;
            try {
                x = userViewModel.getUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (x != null) {
                x.observe(getViewLifecycleOwner(), user -> {
                    // Observe changes in the user data
                    if (user != null && ponto != null) {
                        // Both user and trail data are available, call loadView
                        loadView(view, ponto, user);
                    }
                });

            }
            else{
                loadView(view, ponto,null);
            }
        });

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.conteudo_top, new MediaListPontoFragment(id));
        //transaction.addToBackStack(null);
        transaction.commit();

        //FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        //transaction.add(R.id.pontos_interesse_recycleview, new PontosTrailListFragment(id));
        //transaction.commit();

        ImageButton returnButton = view.findViewById(R.id.returnn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                //requireActivity().getSupportFragmentManager().popBackStack();
                Navigation.findNavController(view).navigate(R.id.action_pontos_fragment_to_explorar_fragment);

            }
        });

        mMapView = view.findViewById(R.id.mapaview3);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        return view;
    }

    private void loadView(View view, Ponto ponto, User user){
        TextView titulo = view.findViewById(R.id.Trilhotitulo);
        titulo.setText(ponto.getPinName());

        TextView desc = view.findViewById(R.id.descricao);
        desc.setText(ponto.getPinDesc());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                LatLng startPoint = new LatLng(ponto.getPinLat(), ponto.getPinLng());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));
                googleMap.addMarker(new MarkerOptions()
                        .position(startPoint)
                        .title(""));
            }
        });

        ImageView startTrail = view.findViewById(R.id.imageButton2);
        startTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("USER",user.toString());
                if (user.getEmail() != null){
                    user.getHistorico_P().add(Integer.parseInt(ponto.getId()));
                    Log.d("HISTORICO",Integer.toString(user.getHistorico_P().get(0)));
                }

                List<Ponto> pontos = new ArrayList<>();
                pontos.add(ponto);

                List<Double> lats = pontos.stream().map(Ponto::getPinLat).collect(Collectors.toList());
                List<Double> lngs = pontos.stream().map(Ponto::getPinLng).collect(Collectors.toList());
                Bundle args = new Bundle();
                args.putDoubleArray("latitude", lats.stream().mapToDouble(Double::doubleValue).toArray());
                args.putDoubleArray("longitude", lngs.stream().mapToDouble(Double::doubleValue).toArray());

                Navigation.findNavController(v).navigate(R.id.action_ponto_fragment_to_mapa_fragment, args);
            }
        });

    }
}