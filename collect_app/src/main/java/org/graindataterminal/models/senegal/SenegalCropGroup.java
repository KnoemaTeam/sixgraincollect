package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SenegalCropGroup implements Cloneable {
    public final static String [] waterSourceIdList = {"pluviale", "puits", "ceanes", "barages", "sondages", "colinaire", "lac", "for_a", "retention", "surface_eau", "sde", "autre_water_source"};

    @SerializedName("crop_name")
    protected String cropName;

    @SerializedName("photo_crop")
    private String cropPhoto;

    @SerializedName("superficie_embave")
    private String cropArea;

    @SerializedName("water")
    private String waterSource;

    @SerializedName("irrigation_type")
    private String irrigationSource;

    @SerializedName("quantite")
    private String harvestedAmount;

    @SerializedName("motif_production_speculation")
    private List<String> mainMotives;

    @SerializedName("plante")
    private String plantedDate;

    @SerializedName("semence")
    private String isSeedUsed;

    @SerializedName("cout_semence")
    private String seedCost;

    @SerializedName("don_semence")
    private String isSeedReceived;

    @SerializedName("semence_don")
    private String seedReceivedAmount;

    public SenegalCropGroup clone () {
        try {
            return (SenegalCropGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropPhoto(String cropPhoto) {
        this.cropPhoto = cropPhoto;
    }

    public String getCropPhoto() {
        return cropPhoto;
    }

    public void setCropArea(String cropArea) {
        this.cropArea = cropArea;
    }

    public String getCropArea() {
        return cropArea;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;

        if (!waterSourceIdList[waterSourceIdList.length - 1].equals(waterSource))
            setIrrigationSource(null);
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setIrrigationSource(String irrigationSource) {
        this.irrigationSource = irrigationSource;
    }

    public String getIrrigationSource() {
        return irrigationSource;
    }

    public void setHarvestedAmount(String harvestedAmount) {
        this.harvestedAmount = harvestedAmount;
    }

    public String getHarvestedAmount() {
        return harvestedAmount;
    }

    public void setMainMotives(List<String> mainMotives) {
        this.mainMotives = mainMotives;
    }

    public List<String> getMainMotives() {
        if (mainMotives == null)
            mainMotives = new ArrayList<>();

        return mainMotives;
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public void setIsSeedUsed(String isSeedUsed) {
        this.isSeedUsed = isSeedUsed;
    }

    public String getIsSeedUsed() {
        return isSeedUsed;
    }

    public void setSeedCost(String seedCost) {
        this.seedCost = seedCost;
    }

    public String getSeedCost() {
        return seedCost;
    }

    public void setIsSeedReceived(String isSeedReceived) {
        this.isSeedReceived = isSeedReceived;

        if (isSeedReceived == null || SenegalSurvey.answerIdList[1].equals(isSeedReceived))
            setSeedReceivedAmount(null);
    }

    public String getIsSeedReceived() {
        return isSeedReceived;
    }

    public void setSeedReceivedAmount(String seedReceivedAmount) {
        this.seedReceivedAmount = seedReceivedAmount;
    }

    public String getSeedReceivedAmount() {
        return seedReceivedAmount;
    }
}
