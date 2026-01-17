package com.example.numediapath.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.numediapath.data.model.RoutePlan;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FirestoreSeeder {

    private static final String TAG = "FirestoreSeeder";

    public static void seedDatabase(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("routes").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    Log.d(TAG, "Base vide. Début du peuplement...");
                    performInjection(context, db);
                } else {
                    Log.d(TAG, "La base contient déjà des données.");
                }
            }
        });
    }

    private static void performInjection(Context context, FirebaseFirestore db) {
        String json = loadJSONFromAsset(context, "init_data.json");

        if (json != null) {
            Gson gson = new Gson();

            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<RoutePlan>>(){}.getType();

            List<RoutePlan> routes = gson.fromJson(json, listType);

            if (routes != null) {
                for (RoutePlan route : routes) {

                    // ✅ AJOUT : Sécurité pour le pays
                    // Si le champ "country" est présent dans ton JSON, Gson l'a déjà rempli.
                    // Si jamais il est vide, on peut forcer une valeur ici (ex: Algérie)
                    if (route.getCountry() == null || route.getCountry().isEmpty()) {
                        route.setCountry("Algérie");
                    }

                    db.collection("routes").document(route.getId()).set(route)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Importé : " + route.getName() + " (" + route.getCountry() + ")"))
                            .addOnFailureListener(e -> Log.e(TAG, "Erreur import : " + route.getName(), e));
                }
            }
        }
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Fichier " + fileName + " manquant dans assets/", ex);
            return null;
        }
    }
}