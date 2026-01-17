package com.example.numediapath.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.numediapath.data.model.POI;
import com.example.numediapath.data.repository.POIRepository;

import java.util.List;

public class POIViewModel extends AndroidViewModel {

    private POIRepository repository;
    private LiveData<List<POI>> allPOIs;

    public POIViewModel(@NonNull Application application) {
        super(application);
        repository = new POIRepository(application);
        allPOIs = repository.getAllPOIs();
    }

    public LiveData<List<POI>> getAllPOIs() {
        return allPOIs;
    }

    // Méthode utile pour ajouter des données de test au début
    public void insert(POI poi) {
        repository.insert(poi);
    }
}