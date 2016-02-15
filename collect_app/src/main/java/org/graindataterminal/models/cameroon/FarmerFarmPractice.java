package org.graindataterminal.models.cameroon;

import com.google.gson.annotations.SerializedName;

public class FarmerFarmPractice implements Cloneable {
    @SerializedName("f_used")
    private String isFungicidesUsed;

    @SerializedName("f_used_saison")
    private String usedFungicidesInSmallSeason;

    @SerializedName("nu_f_appl")
    private String fungicidesNumberInSmallSeason;

    @SerializedName("us_f")
    private String usedFungicidesInBigSeason;

    @SerializedName("n_f_a")
    private String fungicidesNumberInBigSeason;

    @SerializedName("ins_used")
    private String isInsecticideUsed;

    @SerializedName("which_insec")
    private String usedInsecticide;

    @SerializedName("number_ins_app")
    private String insecticideCount;

    @SerializedName("main_issu")
    private String mainCocoaIssue;

    public FarmerFarmPractice clone () {
        try {
            return (FarmerFarmPractice) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setIsFungicidesUsed(String isFungicidesUsed) {
        this.isFungicidesUsed = isFungicidesUsed;
    }

    public String getIsFungicidesUsed() {
        return isFungicidesUsed;
    }

    public void setUsedFungicidesInSmallSeason(String usedFungicidesInSmallSeason) {
        this.usedFungicidesInSmallSeason = usedFungicidesInSmallSeason;
    }

    public String getUsedFungicidesInSmallSeason() {
        return usedFungicidesInSmallSeason;
    }

    public void setFungicidesNumberInSmallSeason(String fungicidesNumberInSmallSeason) {
        this.fungicidesNumberInSmallSeason = fungicidesNumberInSmallSeason;
    }

    public String getFungicidesNumberInSmallSeason() {
        return fungicidesNumberInSmallSeason;
    }

    public void setUsedFungicidesInBigSeason(String usedFungicidesInBigSeason) {
        this.usedFungicidesInBigSeason = usedFungicidesInBigSeason;
    }

    public String getUsedFungicidesInBigSeason() {
        return usedFungicidesInBigSeason;
    }

    public void setFungicidesNumberInBigSeason(String fungicidesNumberInBigSeason) {
        this.fungicidesNumberInBigSeason = fungicidesNumberInBigSeason;
    }

    public String getFungicidesNumberInBigSeason() {
        return fungicidesNumberInBigSeason;
    }

    public void setIsInsecticideUsed(String isInsecticideUsed) {
        this.isInsecticideUsed = isInsecticideUsed;
    }

    public String getIsInsecticideUsed() {
        return isInsecticideUsed;
    }

    public void setUsedInsecticide(String usedInsecticide) {
        this.usedInsecticide = usedInsecticide;
    }

    public String getUsedInsecticide() {
        return usedInsecticide;
    }

    public void setInsecticideCount(String insecticideCount) {
        this.insecticideCount = insecticideCount;
    }

    public String getInsecticideCount() {
        return insecticideCount;
    }

    public void setMainCocoaIssue(String mainCocoaIssue) {
        this.mainCocoaIssue = mainCocoaIssue;
    }

    public String getMainCocoaIssue() {
        return mainCocoaIssue;
    }
}
