package com.example.braguia2.model.App;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ConvertAppTable {
    private static final Gson gson = new Gson();
    @TypeConverter
    public static List<Contacts> contactListFromString(String value) {
        Type listType = new TypeToken<List<Contacts>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String contactListToString(List<Contacts> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Partners> partnerListFromString(String value) {
        Type listType = new TypeToken<List<Partners>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String partnerListToString(List<Partners> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Socials> socialListFromString(String value) {
        Type listType = new TypeToken<List<Socials>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String socialListToString(List<Socials> list) {
        return gson.toJson(list);
    }
}
