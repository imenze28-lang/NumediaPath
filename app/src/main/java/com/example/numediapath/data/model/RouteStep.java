package com.example.numediapath.data.model;
import java.io.Serializable;

public class RouteStep implements Serializable {
    private String time;
    private String title;
    private String description;
    private String iconType;
    private double lat;
    private double lon;
    private boolean visited;

    public RouteStep() {} // Obligatoire pour Firebase

    public RouteStep(String time, String title, String description, String iconType, double lat, double lon) {
        this.time = time;
        this.title = title;
        this.description = description;
        this.iconType = iconType;
        this.lat = lat;
        this.lon = lon;
        this.visited = false;
    }

    // Getters et Setters complets
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
}