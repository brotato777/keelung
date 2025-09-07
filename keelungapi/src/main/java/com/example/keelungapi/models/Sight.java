package com.example.keelungapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sight")
public class Sight {
    @Id
    private String id = null;

    private String sightName = null;
    private String zone = null;
    private String category = null;
    private String photoURL = null;
    private String description = null;
    private String address = null;

    public Sight() {

    }

    public String getId() {
        return id;
    }

    public void setId(final String value) {
        id = value;
    }

    public String getSightName() {
        return sightName;
    }

    public void setSightName(final String value) {
        sightName = value;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(final String value) {
        zone = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String value) {
        category = value;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(final String value) {
        photoURL = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String value) {
        description = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String value) {
        address = value;
    }

    @Override
    public String toString() {
        return "SightName: " + sightName + "\n" +
                "Zone: " + zone + "\n" +
                "Category: " + category + "\n" +
                "PhotoURL: " + photoURL + "\n" +
                "Description: " + description + "\n" +
                "Address: " + address + "\n";
    }
}
