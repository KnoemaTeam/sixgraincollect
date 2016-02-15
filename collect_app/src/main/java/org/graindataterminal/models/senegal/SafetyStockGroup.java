package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SafetyStockGroup implements Cloneable {
    @SerializedName("police_assurance_cheptel")
    private String isLivestockInsurance;

    @SerializedName("comp_assu")
    private String livestockInsuranceCompanyName;

    @SerializedName("cheptel_identification")
    private String hasLivestockIdentification;

    @SerializedName("moy_iden")
    private List<String> livestockIdentificationMethods;

    @SerializedName("autr_moy_iden")
    private String otherLivestockIdentification;

    public SafetyStockGroup clone () {
        try {
            return (SafetyStockGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setIsLivestockInsurance(String isLivestockInsurance) {
        this.isLivestockInsurance = isLivestockInsurance;

        if (isLivestockInsurance == null || SenegalSurvey.answerIdList[1].equals(isLivestockInsurance))
            setLivestockInsuranceCompanyName(null);
    }

    public String getIsLivestockInsurance() {
        return isLivestockInsurance;
    }

    public void setLivestockInsuranceCompanyName(String livestockInsuranceCompanyName) {
        this.livestockInsuranceCompanyName = livestockInsuranceCompanyName;
    }

    public String getLivestockInsuranceCompanyName() {
        return livestockInsuranceCompanyName;
    }

    public void setHasLivestockIdentification(String hasLivestockIdentification) {
        this.hasLivestockIdentification = hasLivestockIdentification;

        if (hasLivestockIdentification == null || SenegalSurvey.answerIdList[1].equals(hasLivestockIdentification)) {
            setLivestockIdentificationMethods(null);
            setOtherLivestockIdentification(null);
        }
    }

    public String getHasLivestockIdentification() {
        return hasLivestockIdentification;
    }

    public void setLivestockIdentificationMethods(List<String> livestockIdentificationMethods) {
        this.livestockIdentificationMethods = livestockIdentificationMethods;

        if (livestockIdentificationMethods == null || !livestockIdentificationMethods.contains(SenegalSurvey.identificationIdList[SenegalSurvey.identificationIdList.length - 1]))
            setOtherLivestockIdentification(null);
    }

    public List<String> getLivestockIdentificationMethods() {
        if (livestockIdentificationMethods == null)
            livestockIdentificationMethods = new ArrayList<>();

        return livestockIdentificationMethods;
    }

    public void setOtherLivestockIdentification(String otherLivestockIdentification) {
        this.otherLivestockIdentification = otherLivestockIdentification;
    }

    public String getOtherLivestockIdentification() {
        return otherLivestockIdentification;
    }
}
