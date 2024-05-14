package com.example.braguia2.model.Trails;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConverteTrailTable {

    @TypeConverter
    public static String fromListEdge(List<Edge> edges) {
        // Convert List<Edge> to a JSON string
        Gson gson = new Gson();
        return gson.toJson(edges);
    }

    @TypeConverter
    public static List<Edge> toListEdge(String json) {
        // Convert JSON string to List<Edge>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Edge>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
