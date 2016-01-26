package com.antoine.startupproject.MapsActivityClass;

import java.util.Comparator;

/**
 * Created by Antoine on 19/01/2016.
 */
public class MarkerInfoTextSearch implements Comparable {

    private String name,vicinity, id;
    private float distance;


    public MarkerInfoTextSearch(String name, String vicinity, float distance, String id){

        this.name = name;
        this.vicinity = vicinity;
        this.distance = distance;
        this.id = id;

    }

    public float getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int compareTo(Object another) {

        MarkerInfoTextSearch m = (MarkerInfoTextSearch) another;

        if(distance<m.getDistance()) return -1;
        if(distance>m.getDistance()) return 1;


        return 0;

    }
}
