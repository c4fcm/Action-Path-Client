package org.actionpath.issues;

import java.util.Date;


public class Issue {
    int id;
    String status;
    String summary;
    String description;
    double latitude;
    double longitude;
    String address;
    String picture;
    Date created_at;
    Date updated_at;
    int place_id;


    public Issue(int id,
            String status,
            String summary,
            String description,
            double latitude,
            double longitude,
            String address,
            String picture,
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
        this.picture = picture;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.place_id = place_id;
    }

    public String getIssueSummary(){
        return this.summary;
    }

    public String getIssueDescription(){
        return this.description;
    }

    public String getIssueAddress(){
        return this.address;
    }

    public void setUniqueID(int id){
        this.id = id;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String toString(){
        return "issue"+this.id;
    }

}
