package com.example.numediapath.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.numediapath.data.local.AppDatabase;
import com.example.numediapath.data.local.POIDao;
import com.example.numediapath.data.model.POI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class POIRepository {

    private final POIDao poiDao;
    private final LiveData<List<POI>> allPOIs;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public POIRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        poiDao = db.poiDao();
        allPOIs = poiDao.getAllPOIs();
    }

    public LiveData<List<POI>> getAllPOIs() {
        return allPOIs;
    }

    public void insert(POI poi) {
        executorService.execute(() -> {
            // Cette méthode existe maintenant dans le POIDao corrigé
            poiDao.insertPOI(poi);
        });
    }
}