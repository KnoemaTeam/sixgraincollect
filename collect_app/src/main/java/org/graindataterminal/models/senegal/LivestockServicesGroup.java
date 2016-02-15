package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LivestockServicesGroup implements Cloneable {
    @SerializedName("r_credit")
    private String hasLivestockCredit;

    @SerializedName("m_credit")
    private String livestockCreditAmount;

    @SerializedName("s_credit")
    private List<String> livestockCreditSource;

    @SerializedName("an_credit")
    private String livestockCreditCompletionDate;

    @SerializedName("pra_credit")
    private String livestockCreditFirstDate;

    @SerializedName("der_an_cre")
    private String livestockCreditLastDate;

    @SerializedName("use_credit")
    private List<String> livestockCreditUsage;

    @SerializedName("info_elevage")
    private List<String> livestockCreditInfo;

    public LivestockServicesGroup clone () {
        try {
            return (LivestockServicesGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setHasLivestockCredit(String hasLivestockCredit) {
        this.hasLivestockCredit = hasLivestockCredit;

        if (hasLivestockCredit == null || SenegalSurvey.answerIdList[1].equals(hasLivestockCredit)) {
            setLivestockCreditAmount(null);
            setLivestockCreditSource(null);
            setLivestockCreditCompletionDate(null);
            setLivestockCreditFirstDate(null);
            setLivestockCreditLastDate(null);
            setLivestockCreditUsage(null);
            setLivestockCreditInfo(null);
        }
    }

    public String getHasLivestockCredit() {
        return hasLivestockCredit;
    }

    public void setLivestockCreditAmount(String livestockCreditAmount) {
        this.livestockCreditAmount = livestockCreditAmount;
    }

    public String getLivestockCreditAmount() {
        return livestockCreditAmount;
    }

    public void setLivestockCreditSource(List<String> livestockCreditSource) {
        this.livestockCreditSource = livestockCreditSource;
    }

    public List<String> getLivestockCreditSource() {
        if (livestockCreditSource == null)
            livestockCreditSource = new ArrayList<>();

        return livestockCreditSource;
    }

    public void setLivestockCreditCompletionDate(String livestockCreditCompletionDate) {
        this.livestockCreditCompletionDate = livestockCreditCompletionDate;
    }

    public String getLivestockCreditCompletionDate() {
        return livestockCreditCompletionDate;
    }

    public void setLivestockCreditFirstDate(String livestockCreditFirstDate) {
        this.livestockCreditFirstDate = livestockCreditFirstDate;
    }

    public String getLivestockCreditFirstDate() {
        return livestockCreditFirstDate;
    }

    public void setLivestockCreditLastDate(String livestockCreditLastDate) {
        this.livestockCreditLastDate = livestockCreditLastDate;
    }

    public String getLivestockCreditLastDate() {
        return livestockCreditLastDate;
    }

    public void setLivestockCreditUsage(List<String> livestockCreditUsage) {
        this.livestockCreditUsage = livestockCreditUsage;
    }

    public List<String> getLivestockCreditUsage() {
        if (livestockCreditUsage == null)
            livestockCreditUsage = new ArrayList<>();

        return livestockCreditUsage;
    }

    public void setLivestockCreditInfo(List<String> livestockCreditInfo) {
        this.livestockCreditInfo = livestockCreditInfo;
    }

    public List<String> getLivestockCreditInfo() {
        if (livestockCreditInfo == null)
            livestockCreditInfo = new ArrayList<>();

        return livestockCreditInfo;
    }
}
