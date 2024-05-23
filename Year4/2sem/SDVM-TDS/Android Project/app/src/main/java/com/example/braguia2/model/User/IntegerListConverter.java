package com.example.braguia2.model.User;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IntegerListConverter {

    @TypeConverter
    public static List<Integer> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Integer> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}