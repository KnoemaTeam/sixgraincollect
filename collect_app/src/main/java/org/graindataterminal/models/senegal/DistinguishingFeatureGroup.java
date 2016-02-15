package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class DistinguishingFeatureGroup implements Cloneable {
    public final static String[] opeIdList = {"ajae", "anprovbs", "fenafils", "anipl", "unce", "asemb", "ipas", "autre_org_el"};

    @SerializedName("fouragere_pratique")
    private String isForagePractice;

    @SerializedName("ope")
    private String ope;

    @SerializedName("organisation_paysane")
    private String peasantOrganizationName;

    public DistinguishingFeatureGroup clone () {
        try {
            return (DistinguishingFeatureGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setIsForagePractice(String isForagePractice) {
        this.isForagePractice = isForagePractice;
    }

    public String getIsForagePractice() {
        return isForagePractice;
    }

    public void setOpe(String ope) {
        this.ope = ope;

        if (ope == null || !opeIdList[opeIdList.length - 1].equals(ope))
            setPeasantOrganizationName(null);
    }

    public String getOpe() {
        return ope;
    }

    public void setPeasantOrganizationName(String peasantOrganizationName) {
        this.peasantOrganizationName = peasantOrganizationName;
    }

    public String getPeasantOrganizationName() {
        return peasantOrganizationName;
    }
}
