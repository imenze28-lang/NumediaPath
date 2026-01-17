package com.example.numediapath.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// Imports des modèles (Entités)
import com.example.numediapath.data.model.POI;
import com.example.numediapath.data.model.RoutePlan;
import com.example.numediapath.data.model.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RoutePlan.class, POI.class, UserProfile.class}, version = 6, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    // ✅ Définition de tous les DAOs nécessaires
    public abstract RouteDao routeDao();
    public abstract POIDao poiDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // ✅ L'exécuteur pour les écritures en arrière-plan (Favoris, etc.)
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "numediapath_db")
                            .fallbackToDestructiveMigration() // Important pour les changements de version
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}