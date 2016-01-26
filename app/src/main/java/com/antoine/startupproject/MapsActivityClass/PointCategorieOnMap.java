package com.antoine.startupproject.MapsActivityClass;

/**
 * Created by Antoine on 02/12/2015.
 */
public class PointCategorieOnMap {

    private String name;
    private boolean visibility = false;


    public PointCategorieOnMap(String name){

        this.name = name;

    }
    public PointCategorieOnMap(String name, boolean visibility){

        this.name = name;
        this.visibility = visibility;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public boolean getVisibility(){
        return visibility;
    }


    public void setVisibility(boolean visible) {
        this.visibility = visible;
    }
}
