package com.example.garbagereporting;

public class Report {
    private String id;
    private String imageUrl;
    private String description;
    private String location;

    // Constructor
    public Report(String id, String imageUrl, String description, String location) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
        this.location = location;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}
