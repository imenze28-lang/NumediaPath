package com.example.numediapath.ui.view.onboarding;

public class PopularDestination {
    public String name, review, imageUrl, profileImageUrl;
    public float rating;

    public PopularDestination(String name, float rating, String review, String imageUrl, String profileImageUrl) {
        this.name = name;
        this.rating = rating;
        this.review = review;
        this.imageUrl = imageUrl;
        this.profileImageUrl = profileImageUrl;
    }
}