package com.example.braguia2.model.User;

import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;


public interface UserAPI {
    @GET("user")
    Call<User> getUser(@Header("Cookie") String csrftoken, @Header("Cookie") String sessionid);
;

    @POST("login")
        Call<User> login(@Body JsonObject login) throws IOException;

    @POST("logout")
    Call<User> logout(@Header("Cookie") String csrftoken, @Header("Cookie") String sessionid);
    ;
}

