package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class Infrastructure implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("infrastructure_name")
    private String name;

    @SerializedName("acquisition_infrastruture")
    private String acquisition;

    @SerializedName("mode_acquisition_infrastructure")
    private String acquisitionMode;

    @SerializedName("cout_infrastructure")
    private String cost;

    public Infrastructure() {

    }

    public Infrastructure clone () {
        try {
            return (Infrastructure) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAcquisition(String acquisition) {
        this.acquisition = acquisition;
    }

    public String getAcquisition() {
        return acquisition;
    }

    public void setAcquisitionMode(String acquisitionMode) {
        this.acquisitionMode = acquisitionMode;
    }

    public String getAcquisitionMode() {
        return acquisitionMode;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }
}
