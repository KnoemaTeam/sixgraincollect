package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SupplementationGroup implements Cloneable {
    public final static String[] compinetationTypeIdList = {"aucun", "s_p_agricole", "pr_transforme", "cmv"};

    @SerializedName("compl_aliment")
    private String supplementUsage;

    @SerializedName("use_complement")
    private List<String> supplementType;

    @SerializedName("s_p_a")
    private List<String> supplementProduct;

    @SerializedName("s_p_u")
    private List<String> supplementFactory;

    @SerializedName("c_m_v")
    private List<String> supplementMinerals;

    public SupplementationGroup clone () {
        try {
            return (SupplementationGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setSupplementUsage(String supplementUsage) {
        this.supplementUsage = supplementUsage;

        if (supplementUsage == null || SenegalSurvey.answerIdList[1].equals(supplementUsage))
            setSupplementType(null);
    }

    public String getSupplementUsage() {
        return supplementUsage;
    }

    public void setSupplementType(List<String> supplementType) {
        this.supplementType = supplementType;

        if (supplementType == null || supplementType.isEmpty()) {
            setSupplementProduct(null);
            setSupplementFactory(null);
            setSupplementMinerals(null);
        }
        else {
            if (!supplementType.contains(compinetationTypeIdList[0]))
                setSupplementProduct(null);
            else if (!supplementType.contains(compinetationTypeIdList[1]))
                setSupplementFactory(null);
            else if (!supplementType.contains(compinetationTypeIdList[2]))
                setSupplementMinerals(null);
        }
    }

    public List<String> getSupplementType() {
        if (supplementType == null)
            supplementType = new ArrayList<>();

        return supplementType;
    }

    public void setSupplementProduct(List<String> supplementProduct) {
        this.supplementProduct = supplementProduct;
    }

    public List<String> getSupplementProduct() {
        if (supplementProduct == null)
            supplementProduct = new ArrayList<>();

        return supplementProduct;
    }

    public void setSupplementFactory(List<String> supplementFactory) {
        this.supplementFactory = supplementFactory;
    }

    public List<String> getSupplementFactory() {
        if (supplementFactory == null)
            supplementFactory = new ArrayList<>();

        return supplementFactory;
    }

    public void setSupplementMinerals(List<String> supplementMinerals) {
        this.supplementMinerals = supplementMinerals;
    }

    public List<String> getSupplementMinerals() {
        if (supplementMinerals == null)
            supplementMinerals = new ArrayList<>();

        return supplementMinerals;
    }
}
