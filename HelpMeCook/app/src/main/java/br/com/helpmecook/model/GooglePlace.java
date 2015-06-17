package br.com.helpmecook.model;

import android.location.Location;

/**
 * Created by Kandarpa on 17/06/2015.
 */
public class GooglePlace {
    private String name;
    private String category;
    private String rating;
    private String open;
    private double lat;
    private double lng;
    private Location location;


    public GooglePlace() {
        this.name = "";
        this.rating = "";
        this.open = "";
        this.setCategory("");
        this.lat = 0;
        this.lng = 0;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setOpenNow(String open) {
        this.open = open;
    }

    public String getOpenNow() {
        return open;
    }
}
