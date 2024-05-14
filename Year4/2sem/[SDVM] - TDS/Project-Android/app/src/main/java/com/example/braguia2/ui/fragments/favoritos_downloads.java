package com.example.braguia2.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.braguia2.R;

public class favoritos_downloads extends Fragment {

    public favoritos_downloads() {
        // Required empty public constructor
    }

    public static favoritos_downloads newInstance() {
        favoritos_downloads fragment = new favoritos_downloads();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favoritos_historico, container, false);

        Button btn_fav = view.findViewById(R.id.btn_favoritos);



        btn_fav.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Navigation.findNavController(view).navigate(R.id.action_favoritos_historico_fragment_to_favoritos_fragment);

                                       }
                                   }
        );


        return  view;
    }
}

