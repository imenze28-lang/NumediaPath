package com.example.numediapath.data.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.numediapath.data.local.AppDatabase;
import com.example.numediapath.data.local.RouteDao;
import com.example.numediapath.data.model.RoutePlan;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouteRepository {
    private static final String TAG = "RouteRepository";
    private final RouteDao routeDao;
    private final LiveData<List<RoutePlan>> allRoutes;
    private final FirebaseFirestore firestore;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public RouteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        routeDao = db.routeDao();
        allRoutes = routeDao.getAllRoutes();
        firestore = FirebaseFirestore.getInstance();

        // Lancement de la synchronisation en temps réel
        syncFromFirestore();
    }

    public LiveData<List<RoutePlan>> getAllRoutes() {
        return allRoutes;
    }

    public LiveData<List<RoutePlan>> getFavoriteRoutes() {
        return routeDao.getFavoriteRoutes();
    }

    /**
     * Alterne l'état favori localement (Room) et sur le Cloud (Firestore)
     */
    public void toggleFavorite(RoutePlan route) {
        executorService.execute(() -> {
            boolean newValue = !route.isFavorite();
            route.setFavorite(newValue);

            // 1. Mise à jour immédiate en local pour la réactivité de l'UI
            routeDao.update(route);

            // 2. Mise à jour Firestore
            firestore.collection("routes")
                    .document(route.getId())
                    .update("isFavorite", newValue)
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur Sync Firestore Favori pour : " + route.getId(), e);
                        // En cas d'échec critique, on pourrait rollback le local ici
                    });
        });
    }

    /**
     * Écoute les changements sur Firestore et met à jour Room.
     * On utilise insertAll qui doit avoir OnConflictStrategy.REPLACE dans le DAO.
     */
    private void syncFromFirestore() {
        firestore.collection("routes").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Échec de l'écoute Firestore.", e);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                executorService.execute(() -> {
                    List<RoutePlan> remoteRoutes = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        RoutePlan route = doc.toObject(RoutePlan.class);
                        if (route != null) {
                            // On force l'ID de l'objet pour qu'il corresponde à l'ID du document Firestore
                            route.setId(doc.getId());
                            remoteRoutes.add(route);
                        }
                    }

                    // On insère tout dans Room.
                    // Room s'occupe de remplacer les anciennes versions par les nouvelles.
                    routeDao.insertAll(remoteRoutes);
                    Log.d(TAG, remoteRoutes.size() + " routes synchronisées depuis Firestore.");
                });
            }
        });
    }

    /**
     * Script pour peupler Firestore à partir d'une liste (ex: chargée depuis init_data.json)
     * Utilise un Batch pour plus d'efficacité et limiter les appels réseau.
     */
    public void seedDatabase(List<RoutePlan> data) {
        if (data == null || data.isEmpty()) return;

        WriteBatch batch = firestore.batch();
        CollectionReference routesRef = firestore.collection("routes");

        for (RoutePlan route : data) {
            // On utilise l'ID défini dans le JSON comme ID de document Firestore
            batch.set(routesRef.document(route.getId()), route);
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Peuplement Firestore réussi !"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors du peuplement Firestore", e));
    }

    public void updateRoute(RoutePlan route) {
        // On exécute la mise à jour en arrière-plan
        AppDatabase.databaseWriteExecutor.execute(() -> {
            routeDao.update(route);
        });
    }
}