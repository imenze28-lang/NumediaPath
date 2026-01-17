package com.example.numediapath.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.numediapath.data.local.Converters;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "routes_table")
@TypeConverters(Converters.class)
public class RoutePlan implements Serializable {
    @PrimaryKey @NonNull
    private String id = "";
    private String name;
    private String type;
    private double totalCost;
    private int totalDuration;
    private int totalDistance;
    private String tags;
    private String imageUrl;
    private boolean isFavorite;
    private String country;
    private List<RouteStep> steps = new ArrayList<>();

    // Sécurité pour les deux formats Firestore
    private String difficultyLabel;
    private int difficultyLevel;

    public RoutePlan() {}

    // Getters / Setters Standard
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }
    public int getTotalDistance() { return totalDistance; }
    public void setTotalDistance(int totalDistance) { this.totalDistance = totalDistance; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public List<RouteStep> getSteps() { return steps; }
    public void setSteps(List<RouteStep> steps) { this.steps = steps; }

    @PropertyName("difficultyLabel")
    public String getDifficultyLabel() { return difficultyLabel; }
    @PropertyName("difficultyLabel")
    public void setDifficultyLabel(String difficultyLabel) { this.difficultyLabel = difficultyLabel; }

    @PropertyName("difficultyLevel")
    public int getDifficultyLevel() { return difficultyLevel; }
    @PropertyName("difficultyLevel")
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }
}