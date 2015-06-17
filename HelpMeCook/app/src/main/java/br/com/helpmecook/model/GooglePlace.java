package br.com.helpmecook.model;

/**
 * Created by Felipe on 03/06/2015.
 */
public class GooglePlace {


    private String name;
    private String category;
    private String rating;
    private String open;
    private double lat;
    private double lon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public GooglePlace(){
        this.name = "";
        this.rating = "";
        this.open = "";
        this.lat = 0;
        this.lon = 0;
        this.setCategory("");
    }
}
