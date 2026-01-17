package com.example.numediapath.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.numediapath.data.model.POI;
import java.util.List;

@Dao
public interface POIDao {

    // Correction : Utilisation de "poi_table" au lieu de "pois"
    @Query("SELECT * FROM poi_table")
    LiveData<List<POI>> getAllPOIs();

    @Query("SELECT * FROM poi_table WHERE id = :id")
    LiveData<POI> getPOI(String id);

    // Ajout de la méthode pour insérer un seul POI
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPOI(POI poi);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<POI> pois);

    @Query("DELETE FROM poi_table")
    void clearAll();
}