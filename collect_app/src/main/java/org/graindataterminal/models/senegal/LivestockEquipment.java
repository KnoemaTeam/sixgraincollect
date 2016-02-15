package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class LivestockEquipment implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("equipement_ferme")
    private String name;

    @SerializedName("nb_equipement")
    private String number;

    @SerializedName("cout_equi_elevage")
    private String cost;

    @SerializedName("age_equipement_elevage")
    private String age;

    @SerializedName("source_financement")
    private String fundingSource;

    @SerializedName("sup_equipement")
    private String areaSize;

    public LivestockEquipment() {

    }

    public LivestockEquipment clone () {
        try {
            return (LivestockEquipment) super.clone();
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

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setAreaSize(String areaSize) {
        this.areaSize = areaSize;
    }

    public String getAreaSize() {
        return areaSize;
    }
}
