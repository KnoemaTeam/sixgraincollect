package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class ProductionEquipment implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("name_materiel")
    private String name;

    @SerializedName("nombre_mteriel_elevage")
    private String number;

    @SerializedName("cout")
    private String cost;

    @SerializedName("age_materiel_elevage")
    private String age;

    @SerializedName("acquisition_materiel_elevage")
    private String operatingEquipment;

    @SerializedName("etat_materiel_elevage")
    private String stateEquipment;

    public ProductionEquipment() {

    }

    public ProductionEquipment clone () {
        try {
            return (ProductionEquipment) super.clone();
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

    public void setOperatingEquipment(String operatingEquipment) {
        this.operatingEquipment = operatingEquipment;
    }

    public String getOperatingEquipment() {
        return operatingEquipment;
    }

    public void setStateEquipment(String stateEquipment) {
        this.stateEquipment = stateEquipment;
    }

    public String getStateEquipment() {
        return stateEquipment;
    }
}
