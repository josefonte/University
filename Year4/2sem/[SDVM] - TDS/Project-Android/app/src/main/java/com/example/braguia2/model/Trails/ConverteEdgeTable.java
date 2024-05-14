package com.example.braguia2.model.Trails;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class ConverteEdgeTable {
    @TypeConverter
    public static Ponto fromStringToPonto(String value) {
        return new Gson().fromJson(value, Ponto.class);
    }

    @TypeConverter
    public static String fromPontoToString(Ponto ponto) {
        Gson gson = new Gson();
        return gson.toJson(ponto);
    }
}
