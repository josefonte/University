package com.example.braguia2.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.braguia2.R;
import com.example.braguia2.viewmodel.UserViewModel;

import java.io.IOException;

public class favoritos_fav extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String param1;
    private String param2;

    public favoritos_fav() {
        // Required empty public constructor
    }

    public static favoritos_fav newInstance(String param1, String param2) {
        favoritos_fav fragment = new favoritos_fav();
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
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favoritos_fav, container, false);

        Button btn_downloads = view.findViewById(R.id.btn_favoritos);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        try {
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (user.getUsername().isEmpty()) {
                    Log.e("FAVORITOS","no login");
                    //Navigation.findNavController(view).navigate(R.id.action_favoritos_fragment_to_login_fragment);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        btn_downloads.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Navigation.findNavController(view).navigate(R.id.action_favoritos_fragment_to_favoritos_downloads_fragment);

                                       }
                                   }
        );



        return  view;
    }

}

