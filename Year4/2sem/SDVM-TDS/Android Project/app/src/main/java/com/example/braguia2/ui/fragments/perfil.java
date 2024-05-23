package com.example.braguia2.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.braguia2.R;
import com.example.braguia2.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.Objects;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class perfil extends Fragment {

    private UserViewModel userViewModel;

    public perfil() {
        // Required empty public constructor
    }


    public static perfil newInstance() {
        return new perfil();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView username = view.findViewById(R.id.nome);
        LinearLayout premium_img = view.findViewById(R.id.btn_premium);
        LinearLayout btnTerminarSessao = view.findViewById(R.id.button_terminarSessao);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        try {
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                Log.e("PERFIL-USER", user.toString());

                if (!user.getUsername().isEmpty()) {
                    Log.e("PERFIL-USER", user.getUsername());
                    username.setText(user.getUsername());
                    if(Objects.equals(user.getUser_type(), "standard") || user.getUsername().equals("standard_user")){
                        premium_img.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    Log.e("PROFILE", user.getUsername());
                    Navigation.findNavController(view).navigate(R.id.action_perfil_fragment_to_login_fragment);
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        btnTerminarSessao.setOnClickListener(v -> {
            try {
                userViewModel.logout(new UserViewModel.LogOutCallback() {
                    @Override
                    public void onLogOutSuccess() {
                        Log.e("AUTH", "LOGOUT SUCCESS");
                        Navigation.findNavController(view).navigate(R.id.action_perfil_fragment_to_login_fragment);
                    }

                    @Override
                    public void onLogOutFailure() {
                        Log.e("AUTH", "LOGOUT FAILED");
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        LinearLayout btnConfigs = view.findViewById(R.id.btn_config);

        btnConfigs.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_perfil_fragment_to_configs);
        });


        LinearLayout btnApoio = view.findViewById(R.id.btn_apoio);
        btnApoio.setOnClickListener(v ->{
            Navigation.findNavController(view).navigate(R.id.action_perfil_fragment_to_suport);
        });

        LinearLayout btnSobre = view.findViewById(R.id.btn_sobre);
        btnSobre.setOnClickListener(v->{
            Navigation.findNavController(view).navigate(R.id.action_perfil_fragment_to_sobre_fragment);
        });


    }

}