package com.example.braguia2.model.App;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AppAPI {
    @GET("app")
    Call<List<App>> getApp() throws IOException;
}