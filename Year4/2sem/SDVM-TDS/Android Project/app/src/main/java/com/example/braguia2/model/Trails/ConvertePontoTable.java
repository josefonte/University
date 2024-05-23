package com.example.braguia2.model.Trails;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConvertePontoTable {
    @TypeConverter
    public static List<RelPin> fromStringToRelPinList(String value) {
        Type listType = new TypeToken<List<RelPin>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromRelPinListToString(List<RelPin> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Conteudo> fromStringToConteudoList(String value) {
        Type listType = new TypeToken<List<Conteudo>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromConteudoListToString(List<Conteudo> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}