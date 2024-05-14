package com.example.braguia2.ui.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Trail;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class explorar extends Fragment {

    private TextView textViewPop1;
    private TextView stat11;
    private TextView stat12;
    private TextView stat13;
    private TextView stat14;

    private ImageView imageViewPop1;
    private ImageButton telemovelEmergencia;

    private ImageView currentFilter;

    public explorar() {
        // Required empty public constructor
    }

    public static explorar newInstance(String param1, String param2) {
        explorar fragment = new explorar();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explorar, container, false);


        textViewPop1 = view.findViewById(R.id.textViewPop1);
        stat11 = view.findViewById(R.id.stat11);
        stat12 = view.findViewById(R.id.stat12);
        stat13 = view.findViewById(R.id.stat13);
        stat14 = view.findViewById(R.id.stat14);
        imageViewPop1 = view.findViewById(R.id.imageViewPop1);
        telemovelEmergencia = view.findViewById(R.id.phone);

        TrailsViewModel trailsViewModel = new ViewModelProvider(requireActivity()).get(TrailsViewModel.class);
        trailsViewModel.getAllTrails().observe(getViewLifecycleOwner(), new Observer<List<Trail>>() {
            @Override
            public void onChanged(List<Trail> trails) {
                // Check if trails list is not empty
                if (!trails.isEmpty()) {
                    Trail firstTrail = trails.get(0);
                    textViewPop1.setText(firstTrail.getTrailName() );
                    stat11.setText(Integer.toString(firstTrail.getTrailDuration()));
                    stat12.setText(Double.toString(firstTrail.getTrailDistance()));
                    int edgesSize = firstTrail.getEdges().size() + 1;
                    stat13.setText(Integer.toString(edgesSize));
                    stat14.setText(firstTrail.getTrailDifficultyExtenso());
                    Picasso.get()
                            .load(firstTrail.getUrl())
                            .into(imageViewPop1);



                    imageViewPop1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putInt("trailId", 1);

                            Navigation.findNavController(view).navigate(R.id.action_explorar_fragment_to_trail_fragment,args);
                        }
                    });

                    telemovelEmergencia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Navigate to the new fragment
                            Navigation.findNavController(view).navigate(R.id.action_explorar_fragment_to_suport);

                        }
                    });
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation animShrink = AnimationUtils.loadAnimation(getContext(), R.anim.shrink);
        currentFilter = view.findViewById(R.id.natureza_filter);
        ImageView filtro_natureza = view.findViewById(R.id.natureza_filter);
        ImageView filtro_cultura = view.findViewById(R.id.cultura_filter);
        ImageView filtro_religiao = view.findViewById(R.id.religiao_filter);
        ImageView filtro_comida = view.findViewById(R.id.comida_filter);
        ImageView filtro_bebida = view.findViewById(R.id.bebida_filter);

        filtro_natureza.setOnClickListener(v -> {
            changeCurrent(view);
            currentFilter = filtro_natureza;
            filtro_natureza.setImageResource(R.drawable.natureza_filter_sel);

            filtro_natureza.startAnimation(animShrink);
        });
        filtro_cultura.setOnClickListener(v -> {
            changeCurrent(view);
            currentFilter = filtro_cultura;
            filtro_cultura.setImageResource(R.drawable.cultura_filter_sel);

            filtro_cultura.startAnimation(animShrink);
        });
        filtro_religiao.setOnClickListener(v -> {
            changeCurrent(view);
            currentFilter = filtro_religiao;
            filtro_religiao.setImageResource(R.drawable.religiao_filter_sel);

            filtro_religiao.startAnimation(animShrink);
        });
        filtro_comida.setOnClickListener(v -> {
            changeCurrent(view);
            currentFilter = filtro_comida;
            filtro_comida.setImageResource(R.drawable.comida_filter_sel);
            filtro_comida.startAnimation(animShrink);
        });
        filtro_bebida.setOnClickListener(v -> {
            changeCurrent(view);
            currentFilter = filtro_bebida;
            filtro_bebida.setImageResource(R.drawable.bebida_filter_sel);
            filtro_bebida.startAnimation(animShrink);
        });





    }

    private void changeCurrent(View view){

        if(currentFilter ==  view.findViewById(R.id.natureza_filter)){
                currentFilter.setImageResource(R.drawable.natureza_filter_not);
        }
        if(currentFilter ==  view.findViewById(R.id.comida_filter)){
            currentFilter.setImageResource(R.drawable.comida_filter_not);
        }
        if(currentFilter ==  view.findViewById(R.id.cultura_filter)){
            currentFilter.setImageResource(R.drawable.cultura_filter_not);
        }
        if(currentFilter ==  view.findViewById(R.id.religiao_filter)){
            currentFilter.setImageResource(R.drawable.religiao_filter_not);
        }
        if(currentFilter ==  view.findViewById(R.id.bebida_filter)){
            currentFilter.setImageResource(R.drawable.bebida_filter_not);
        }
    }
}



