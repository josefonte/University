package com.example.braguia2.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.braguia2.model.User.User;

import com.example.braguia2.repositories.UserRepository;

import java.io.IOException;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private LiveData<User> user;
    public  String user_username;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository= new UserRepository(application);

        Log.e("VIEWMODEL","New user View Model");

        user = repository.getUser();
        user_username = repository.lastLoggedUser;
        User loggedInUser = user.getValue();
        if(loggedInUser != null) {
            Log.e("VIEWMODEL", "Logged in user: " + loggedInUser.getUsername());
        } else {
            Log.e("VIEWMODEL", "No user logged in");
        }
    }

    public LiveData<User> getUser() throws IOException {
        return user;
    }

    public void login(String username, String password, final LoginCallback callback) throws IOException {

        repository.loginRequest(username,password, new UserRepository.LoginCallback() {
            @Override
            public void onLoginSuccess(User userlogin) {
                user = repository.getUser();
                callback.onLoginSuccess(); }

            @Override
            public void onLoginError(Exception e) { callback.onLoginFailure(); }

        });
    }



    public void logout( final LogOutCallback callback) throws IOException {
        repository.logoutRequest( new UserRepository.LogoutCallback() {
            @Override
            public void onLogoutSuccess() {callback.onLogOutSuccess();}

            @Override
            public void onLogoutError() {callback.onLogOutFailure();}

        });
    }

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure();
    }
    public interface LogOutCallback {
        void onLogOutSuccess();
        void onLogOutFailure();
    }
}
