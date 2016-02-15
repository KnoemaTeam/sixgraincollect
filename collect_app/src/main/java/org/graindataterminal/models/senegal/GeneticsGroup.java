package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GeneticsGroup implements Cloneable {
    public final static String[] geneticIdList = {"aucune_pratique_gen", "intro_geniteur", "insemination", "mixte"};
    public final static String[] controlIdList = {"aucune_controle", "par_jour", "par_semaine", "par_mois", "par_bimestre", "par_trimestre", "autre_controle"};

    @SerializedName("prati_genetique")
    private String geneticsUsage;

    @SerializedName("esp_gen")
    private List<String> geneticsType;

    @SerializedName("contr_sante")
    private String sanitarControlUsage;

    @SerializedName("esp_cont")
    private List<String> sanitarControlType;

    public VaccineGroup clone () {
        try {
            return (VaccineGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setGeneticsUsage(String geneticsUsage) {
        this.geneticsUsage = geneticsUsage;
    }

    public String getGeneticsUsage() {
        return geneticsUsage;
    }

    public void setGeneticsType(List<String> geneticsType) {
        this.geneticsType = geneticsType;
    }

    public List<String> getGeneticsType() {
        if (geneticsType == null)
            geneticsType = new ArrayList<>();

        return geneticsType;
    }

    public void setSanitarControlUsage(String sanitarControlUsage) {
        this.sanitarControlUsage = sanitarControlUsage;
    }

    public String getSanitarControlUsage() {
        return sanitarControlUsage;
    }

    public void setSanitarControlType(List<String> sanitarControlType) {
        this.sanitarControlType = sanitarControlType;
    }

    public List<String> getSanitarControlType() {
        if (sanitarControlType == null)
            sanitarControlType = new ArrayList<>();

        return sanitarControlType;
    }
}
