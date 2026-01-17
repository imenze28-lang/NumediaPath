package com.example.numediapath.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.numediapath.data.model.RoutePlan;
import com.example.numediapath.data.repository.RouteRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteViewModel extends AndroidViewModel {

    private final RouteRepository repository;
    private final LiveData<List<RoutePlan>> allRoutes;
    private final MediatorLiveData<List<RoutePlan>> filteredRoutes = new MediatorLiveData<>();
    private String selectedCountry = "";

    // Variables pour mémoriser les derniers filtres appliqués
    private Integer lastBudget = null;
    private Integer lastDuration = null;
    private Integer lastEffort = null;
    private ArrayList<String> lastActivities = null;

    public RouteViewModel(Application application) {
        super(application);
        repository = new RouteRepository(application);
        allRoutes = repository.getAllRoutes();

        // Observation de la source principale : Applique les filtres dès que Room envoie des données
        filteredRoutes.addSource(allRoutes, routes -> applyFilters());
    }

    // ==========================================
    // MÉTHODES POUR L'UI (NE PAS SUPPRIMER)
    // ==========================================

    public LiveData<List<RoutePlan>> getFilteredRoutes() {
        return filteredRoutes;
    }

    public LiveData<List<RoutePlan>> getAllRoutes() {
        return allRoutes;
    }

    // ✅ Crucial pour le ProfileFragment
    public LiveData<List<RoutePlan>> getFavoriteRoutes() {
        return repository.getFavoriteRoutes();
    }

    public void toggleFavorite(RoutePlan route) {
        repository.updateRoute(route);
    }

    // ==========================================
    // LOGIQUE DE FILTRAGE ET TRI
    // ==========================================

    public void generateFilteredRoutes(int budget, int duration, int effort, ArrayList<String> activities) {
        this.lastBudget = budget;
        this.lastDuration = duration;
        this.lastEffort = effort;
        this.lastActivities = activities;
        applyFilters();
    }

    public void setSelectedCountry(String country) {
        this.selectedCountry = country;
        // On réapplique les filtres dès que le pays change
        applyFilters();
    }

    public String getSelectedCountry() { return selectedCountry; }

    private void applyFilters() {
        List<RoutePlan> source = allRoutes.getValue();
        if (source == null) return;

        List<RoutePlan> result = new ArrayList<>();

        for (RoutePlan route : source) {

            // ✅ AJOUT : FILTRAGE PAR PAYS
            // Si un pays est sélectionné, on ignore tout ce qui ne correspond pas
            if (selectedCountry != null && !selectedCountry.isEmpty()) {
                if (route.getCountry() == null || !route.getCountry().equalsIgnoreCase(selectedCountry)) {
                    continue;
                }
            }

            // On garde ta logique de calcul de difficulté pour le tri même si les filtres ne sont pas mis
            int difficulty;
            double dist = route.getTotalDistance();
            if (dist <= 6.0) difficulty = 1;      // COOL
            else if (dist <= 15.0) difficulty = 2; // ACTIF
            else difficulty = 3;                  // SPORTIF

            route.setDifficultyLevel(difficulty);

            // ✅ FILTRAGE CUMULATIF (Seulement si l'utilisateur a validé ses préférences)
            if (lastBudget != null) {
                if (difficulty > lastEffort) continue;
                if (route.getTotalCost() > lastBudget) continue;
                if (route.getTotalDuration() > lastDuration) continue;

                // ✅ LOGIQUE FLEXIBLE DES ACTIVITÉS ("OU")
                if (lastActivities != null && !lastActivities.isEmpty()) {
                    boolean matchAny = false;
                    String rawTags = route.getTags() != null ? route.getTags().toLowerCase() : "";
                    for (String activity : lastActivities) {
                        if (rawTags.contains(activity.toLowerCase())) {
                            matchAny = true;
                            break;
                        }
                    }
                    if (!matchAny) continue;
                }
            }

            result.add(route);
        }

        // ✅ TRI PAR PERTINENCE (Conservé tel quel)
        Collections.sort(result, (r1, r2) -> {
            int diffComp = Integer.compare(r2.getDifficultyLevel(), r1.getDifficultyLevel());
            if (diffComp != 0) return diffComp;
            return Double.compare(r2.getTotalDistance(), r1.getTotalDistance());
        });

        filteredRoutes.setValue(result);
    }
}