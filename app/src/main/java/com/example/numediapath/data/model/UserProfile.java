package com.example.numediapath.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "user_profile")
public class UserProfile implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String profileImageUrl; // ✅ AJOUTÉ : Champ pour l'image
    private int budgetMax;
    private int effortLevel;
    private int duration;
    private String selectedActivities;

    public UserProfile() {
    }

    // ✅ CONSTRUCTEUR MIS À JOUR
    public UserProfile(@NonNull String id, String name, String profileImageUrl, int budgetMax, int effortLevel, int duration, String selectedActivities) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.budgetMax = budgetMax;
        this.effortLevel = effortLevel;
        this.duration = duration;
        this.selectedActivities = selectedActivities;
    }

    // --- GETTERS & SETTERS ---

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // ✅ NOUVEAU GETTER
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public int getBudgetMax() { return budgetMax; }
    public void setBudgetMax(int budgetMax) { this.budgetMax = budgetMax; }

    public int getEffortLevel() { return effortLevel; }
    public void setEffortLevel(int effortLevel) { this.effortLevel = effortLevel; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getSelectedActivities() { return selectedActivities; }
    public void setSelectedActivities(String selectedActivities) { this.selectedActivities = selectedActivities; }
}