package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VaccineGroup implements Cloneable {
    public final static String[] vaccinationIdList = {"aucune_vaccin", "systematique", "occasionnelle"};

    @SerializedName("vac")
    private String vaccinesUsage;

    @SerializedName("es_vac")
    private List<String> vaccinesType;

    @SerializedName("deparasite")
    private String dewormingUsage;

    @SerializedName("esp_dep")
    private List<String> dewormingType;

    public VaccineGroup clone () {
        try {
            return (VaccineGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setVaccinesUsage(String vaccinesUsage) {
        this.vaccinesUsage = vaccinesUsage;
    }

    public String getVaccinesUsage() {
        return vaccinesUsage;
    }

    public void setVaccinesType(List<String> vaccinesType) {
        this.vaccinesType = vaccinesType;
    }

    public List<String> getVaccinesType() {
        if (vaccinesType == null)
            vaccinesType = new ArrayList<>();

        return vaccinesType;
    }

    public void setDewormingUsage(String dewormingUsage) {
        this.dewormingUsage = dewormingUsage;

        if (dewormingUsage == null || SenegalSurvey.answerIdList[1].equals(dewormingUsage))
            setDewormingType(null);
    }

    public String getDewormingUsage() {
        return dewormingUsage;
    }

    public void setDewormingType(List<String> dewormingType) {
        this.dewormingType = dewormingType;
    }

    public List<String> getDewormingType() {
        if (dewormingType == null)
            dewormingType = new ArrayList<>();

        return dewormingType;
    }
}
