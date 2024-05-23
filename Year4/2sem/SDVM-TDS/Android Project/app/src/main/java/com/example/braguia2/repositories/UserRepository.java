package com.example.braguia2.repositories;

import android.app.Application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


import com.example.braguia2.model.User.User;
import com.example.braguia2.model.User.UserAPI;
import com.example.braguia2.model.User.UserDAO;
import com.example.braguia2.model.GuideDatabase;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;



public class UserRepository {

    private final GuideDatabase database;
    private UserDAO userDAO;
    private MediatorLiveData<User> user;
    private final Retrofit retrofit;
    private final UserAPI api;
    private final SharedPreferences sharedPreferences;

    public String lastLoggedUser;


    public UserRepository(Application application){
        database = GuideDatabase.getInstance(application);
        userDAO = database.userDAO();
        user = new MediatorLiveData<>();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://764f-193-137-92-72.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UserAPI.class);

        sharedPreferences = application.getApplicationContext().getSharedPreferences("LocalPreferences", Context.MODE_PRIVATE);
        String csrfToken = sharedPreferences.getString("cookie-csrftoken", "");
        String sessionId = sharedPreferences.getString("cookie-sessionId", "");
        lastLoggedUser = sharedPreferences.getString("lastLoggedUser","");

        Log.e("cookie-sessionId", sharedPreferences.getString("cookie-sessionId", ""));
        Log.e("cookie-csrftoken", sharedPreferences.getString("cookie-csrftoken", ""));
        Log.e("lastLoggedUser",lastLoggedUser);

         updateUser(csrfToken, sessionId,new LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                Log.e("START-SUCCESS", user.toString());
            }

            @Override
            public void onLoginError(Exception e) {
                Log.e("START-FAILED", user.toString());

            }
        });

        user.addSource(userDAO.getUser(lastLoggedUser),localUser -> {
            if(lastLoggedUser==null || lastLoggedUser.isEmpty()){
                user.postValue(new User("", "loggedOff"));
                Log.e("lastloggeduser-add-source1", "user-dao");
            }
            if(localUser!=null){
                user.postValue(localUser);
                Log.e("LOCAL_USER-source2", "user-dao");
            }
        });
    }



    public void loginRequest(String username, String password,final LoginCallback callback) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("email", "");
        body.addProperty("password", password);
        Log.e("LOGIN_REQUEST", body.toString());

        api.login(body).enqueue(new retrofit2.Callback<User>() {

            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    List<String> cookies  = headers.values("Set-Cookie");
                    String cookie_csrftoken = cookies.get(0);
                    String cookie_sessionId = cookies.get(1);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cookie-csrftoken", cookie_csrftoken);
                    editor.putString("cookie-sessionId", cookie_sessionId);
                    editor.apply();


                    Log.e("cookie-sessionId", cookie_sessionId );
                    Log.e("cookie-csrftoken", cookie_csrftoken);

                    updateUser(cookie_csrftoken,cookie_sessionId,callback);
                    callback.onLoginSuccess(response.body());
                } else {

                    callback.onLoginError(new Exception("Login failed"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("login-REPO", "FAILED" );
                callback.onLoginError(new IOException("Network error"));
            }
        });

    }

    public void logoutRequest(final LogoutCallback callback){

        String cookie_csrfToken = sharedPreferences.getString("cookie-csrftoken", "");
        String cookie_sessionId = sharedPreferences.getString("cookie-sessionId", "");
        api.logout(cookie_csrfToken, cookie_sessionId).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                lastLoggedUser = "";
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cookie-csrftoken", "");
                editor.putString("cookie-sessionId","");
                editor.putString("lastLoggedUser","");
                editor.apply();



                Log.e("AUTH-logout", "Cookies cleaned and logged out complete");

                callback.onLogoutSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("AUTH", "logged out failed ");
                callback.onLogoutError();
            }


        });

    }


    public void updateUser(String csrfToken, String sessionId, LoginCallback callback) {
        Log.e("UPDATE USER", "updating user | token:" + csrfToken + "  \n session: " + sessionId);

        if (csrfToken != "" && sessionId!="") {
            Log.e("DEBUG", "Cookies:" + csrfToken + " | " + sessionId);
            api.getUser(csrfToken, sessionId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.e("UPDATE-USER", "Response Body: " + response.body());
                    if (response.isSuccessful()) {
                        User user_response = (User) response.body();
                        insert(user_response);
                        lastLoggedUser = user_response.getUsername();
                        sharedPreferences.edit().putString("lastLoggedUser", lastLoggedUser).apply();
                        callback.onLoginSuccess(user_response);
                    }

                    else{
                        callback.onLoginError(new Exception("Login failed"));
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    callback.onLoginError(new IOException("Network error"));
                }

            });
        }
    }
    public LiveData<User> getUser() {
        return user;
    }
    public void setUser(User fixedUser) {
        this.user = new MediatorLiveData<>();
        this.user.postValue(fixedUser);
    }
    public void insert(User user){
        new UserRepository.InsertAsyncTask(userDAO).execute(user);
    }

    private static class InsertAsyncTask extends AsyncTask<User, Void, Void> {
        private final UserDAO userDAO;

        public InsertAsyncTask(UserDAO user) {
            this.userDAO = user;
        }

        @Override
        protected Void doInBackground(User... users) {
            Log.e("INSERT-USER", users[0].toString());
            userDAO.insertOrUpdate(users[0]);

            return null;
        }
    }



    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginError(Exception e);
    }

    public interface LogoutCallback {
        void onLogoutSuccess();

        void onLogoutError();
    }


}
