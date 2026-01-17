package com.example.numediapath.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "poi_table")
public class POI implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String category;

    // Constructeur vide pour Room/Firebase
    public POI() {
        this.id = "";
    }

    public POI(@NonNull String id, String name, String description, double latitude, double longitude, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters et Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}