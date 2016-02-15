package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class Movement implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("es_animal")
    private String species;

    @SerializedName("nd_naissance")
    private String birthNumber;

    @SerializedName("heritage")
    private String heritage;

    @SerializedName("ach")
    private String shopping;

    @SerializedName("don")
    private String donations;

    @SerializedName("confiage")
    private String confiage;

    @SerializedName("aut_entrees")
    private String otherEntries;

    public Movement clone () {
        try {
            return (Movement) super.clone();
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

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpecies() {
        return species;
    }

    public void setBirthNumber(String birthNumber) {
        this.birthNumber = birthNumber;
    }

    public String getBirthNumber() {
        return birthNumber;
    }

    public void setHeritage(String heritage) {
        this.heritage = heritage;
    }

    public String getHeritage() {
        return heritage;
    }

    public void setShopping(String shopping) {
        this.shopping = shopping;
    }

    public String getShopping() {
        return shopping;
    }

    public void setDonations(String donations) {
        this.donations = donations;
    }

    public String getDonations() {
        return donations;
    }

    public void setConfiage(String confiage) {
        this.confiage = confiage;
    }

    public String getConfiage() {
        return confiage;
    }

    public void setOtherEntries(String otherEntries) {
        this.otherEntries = otherEntries;
    }

    public String getOtherEntries() {
        return otherEntries;
    }
}
