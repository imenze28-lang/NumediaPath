package com.example.numediapath.data.local;

import androidx.room.TypeConverter;
import com.example.numediapath.data.model.RouteStep;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    @TypeConverter
    public static String fromRouteStepList(List<RouteStep> steps) {
        if (steps == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(steps);
    }

    @TypeConverter
    public static List<RouteStep> toRouteStepList(String stepsString) {
        if (stepsString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RouteStep>>() {}.getType();
        return gson.fromJson(stepsString, type);
    }
}