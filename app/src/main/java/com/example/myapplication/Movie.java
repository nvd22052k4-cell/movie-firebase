package com.example.myapplication;

public class Movie {
    private String id;
    private String title;
    private String description;
    private String posterUrl;
    private double rating;
    private String genre;
    private double price;

    public Movie() {} // Required for Firebase

    public Movie(String id, String title, String description, String posterUrl, double rating, String genre, double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.genre = genre;
        this.price = price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}