package com.example.braguia2.model.Trails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrailAPI {

    @GET("trails")
    Call<List<Trail>> getTrails();

}