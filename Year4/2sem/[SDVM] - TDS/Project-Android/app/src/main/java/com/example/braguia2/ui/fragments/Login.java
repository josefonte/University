package com.example.braguia2.ui.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.braguia2.R;
import com.example.braguia2.viewmodel.UserViewModel;

import java.io.IOException;

public class Login extends Fragment {
    public Login() {
    }

    public static Login newInstance() {
        return new Login();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText username = view.findViewById(R.id.username);
        EditText password = view.findViewById(R.id.password);
        Button btnIniciarSessao = view.findViewById(R.id.button_iniciarSessao);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        try {
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (!user.getUsername().isEmpty()) {
                    Navigation.findNavController(view).navigate(R.id.action_login_fragment_to_perfil_fragment);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        btnIniciarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user =  username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                Log.e("UI - Username",user);
                Log.e("UI - Password", pass);

                try {
                    userViewModel.login(user, pass, new UserViewModel.LoginCallback() {
                        @Override
                        public void onLoginSuccess() {
                            Log.e("UI-AUTH","LOGIN SUCCESS");
                            Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_perfil_fragment);
                        }

                        @Override
                        public void onLoginFailure() {
                            Log.e("UI-AUTH","LOGIN FAILED");
                            username.setBackgroundResource(R.drawable.edittext_outline_red);
                            password.setBackgroundResource(R.drawable.edittext_outline_red);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        try {
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (!user.getUsername().isEmpty()) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_perfil_fragment);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}


