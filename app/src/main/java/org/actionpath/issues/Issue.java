package org.actionpath.issues;

import java.util.Date;


public class Issue {

    public static final int DEFAULT_RADIUS = 500;

    int id;
    String status;
    String summary;
    String description;
    double latitude;
    double longitude;
    String address;
    String imageUrl;
    Date createdAt;
    Date updatedAt;
    int placeId;
    int radiusMeters = DEFAULT_RADIUS;
    boolean test = false;

    public Issue(int id,
            String status,
            String summary,
            String description,
            double latitude,
            double longitude,
            String address,
            String imageUrl,
            Date created_at,
            Date updated_at,
            int place_id){
        this.id = id;
        this.status = status;
        this.summary = summary;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imageUrl = imageUrl;
        this.createdAt = created_at;
        this.updatedAt = updated_at;
        this.placeId = place_id;
    }

    public String getIssueSummary(){
        return this.summary;
    }

    public String getIssueDescription(){
        return this.description;
    }

    public String getIssueAddress(){ return this.address; }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String toString(){
        return "issue"+this.id;
    }

    public int getId(){ return id; }

    public int getRadius() { return radiusMeters; }

    public void setTest(boolean isATestIssue){ test = isATestIssue; }

    public boolean isTest() { return test; }

    public String getImageUrl() {return imageUrl; }

    public boolean hasImageUrl() {return imageUrl.length()>0; }

    public String getStatus() { return status; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int getPlaceId(){
        return placeId;
    }

}


