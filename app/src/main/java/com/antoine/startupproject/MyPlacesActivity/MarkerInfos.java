package com.antoine.startupproject.MyPlacesActivity;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.antoine.startupproject.MapsActivityClass.MarkerInfoTextSearch;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Antoine on 26/12/2015.
 */
public class MarkerInfos implements Parcelable, Comparable {


    private String id;
    private Double lat;
    private Double lng;
    private String name;
    private String[] categorie;
    private String isOpen;
    private String vicinity;
    private float distance;

    public MarkerInfos(String id, Double lat, Double lng, String name, String[] categorie, String isOpen, String vicinity){

        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.categorie = categorie;
        this.isOpen = isOpen;
        this.vicinity = vicinity;
    }

    public MarkerInfos(String id, Double lat, Double lng, String name, String[] categorie, String isOpen, String vicinity, float distance){

        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.categorie = categorie;
        this.isOpen = isOpen;
        this.vicinity = vicinity;
        this.distance = distance;

    }

    public MarkerInfos(Parcel parcel){

        id = parcel.readString();
        lat = parcel.readDouble();
        lng = parcel.readDouble();
        name = parcel.readString();
        categorie = parcel.createStringArray();
        isOpen = parcel.readString();
        vicinity = parcel.readString();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String[] getCategorie() {
        return categorie;
    }
    public void setCategorie(String[] categorie) {
        this.categorie = categorie;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String photoReference) {
        this.vicinity = photoReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeStringArray(categorie);
        dest.writeString(isOpen);
        dest.writeString(vicinity);


    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MarkerInfos createFromParcel(Parcel in) {
            return new MarkerInfos(in);
        }

        public MarkerInfos[] newArray(int size) {
            return new MarkerInfos[size];
        }
    };

    @Override
    public int compareTo(Object another) {

        MarkerInfos m = (MarkerInfos) another;

        if(distance<m.getDistance()) return -1;
        if(distance>m.getDistance()) return 1;

        return 0;
    }
}
