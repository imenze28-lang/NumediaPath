package com.example.numediapath.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.numediapath.data.model.RoutePlan;

import java.util.List;

@Dao
public interface RouteDao {

    // Récupère tous les trajets mis en cache depuis Firebase
    @Query("SELECT * FROM routes_table")
    LiveData<List<RoutePlan>> getAllRoutes();

    // Récupère uniquement les trajets marqués comme favoris
    @Query("SELECT * FROM routes_table WHERE isFavorite = 1")
    LiveData<List<RoutePlan>> getFavoriteRoutes();

    // Insère ou remplace une liste complète (utile pour la synchronisation Firestore -> Room)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RoutePlan> routes);

    // Insère ou remplace un seul trajet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RoutePlan route);

    // Met à jour un trajet (essentiel pour le toggle des favoris)
    @Update
    void update(RoutePlan route);

    // Vide la table locale (utile lors d'une déconnexion ou d'un rafraîchissement total)
    @Query("DELETE FROM routes_table")
    void deleteAll();

    @Query("SELECT * FROM routes_table WHERE " +
            "(tags LIKE '%' || :country || '%' OR name LIKE '%' || :country || '%') " +
            "AND totalCost <= :maxPrice " +
            "AND difficultyLevel <= :maxDifficulty")
    LiveData<List<RoutePlan>> getFilteredRoutes(String country, float maxPrice, int maxDifficulty);
}