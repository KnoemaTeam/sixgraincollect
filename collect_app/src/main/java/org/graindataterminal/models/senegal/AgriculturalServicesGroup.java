package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AgriculturalServicesGroup implements Cloneable {
    @SerializedName("credit")
    private String hasCredit;

    @SerializedName("credit_obtenu")
    private List<String> creditType;

    @SerializedName("montant_credit")
    private String creditAmount;

    @SerializedName("credit_source")
    private List<String> creditSource;

    @SerializedName("annee_credit")
    private String creditCompletionDate;

    @SerializedName("first_remboursement")
    private String creditFirstDateRepayment;

    @SerializedName("last_remboursement")
    private String creditLastDateRepayment;

    @SerializedName("credit_dest")
    private List<String> creditDestinations;

    public AgriculturalServicesGroup clone () {
        try {
            return (AgriculturalServicesGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setHasCredit(String hasCredit) {
        this.hasCredit = hasCredit;

        if (hasCredit == null || SenegalSurvey.answerIdList[1].equals(hasCredit)) {
            setCreditType(null);
            setCreditAmount(null);
            setCreditSource(null);
            setCreditCompletionDate(null);
            setCreditFirstDateRepayment(null);
            setCreditLastDateRepayment(null);
            setCreditDestinations(null);
        }
    }

    public String getHasCredit() {
        return hasCredit;
    }

    public void setCreditType(List<String> creditType) {
        this.creditType = creditType;
    }

    public List<String> getCreditType() {
        if (creditType == null)
            creditType = new ArrayList<>();

        return creditType;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditSource(List<String> creditSource) {
        this.creditSource = creditSource;
    }

    public List<String> getCreditSource() {
        if (creditSource == null)
            creditSource = new ArrayList<>();

        return creditSource;
    }

    public void setCreditCompletionDate(String creditCompletionDate) {
        this.creditCompletionDate = creditCompletionDate;
    }

    public String getCreditCompletionDate() {
        return creditCompletionDate;
    }

    public void setCreditFirstDateRepayment(String creditFirstDateRepayment) {
        this.creditFirstDateRepayment = creditFirstDateRepayment;
    }

    public String getCreditFirstDateRepayment() {
        return creditFirstDateRepayment;
    }

    public void setCreditLastDateRepayment(String creditLastDateRepayment) {
        this.creditLastDateRepayment = creditLastDateRepayment;
    }

    public String getCreditLastDateRepayment() {
        return creditLastDateRepayment;
    }

    public void setCreditDestinations(List<String> creditDestinations) {
        this.creditDestinations = creditDestinations;
    }

    public List<String> getCreditDestinations() {
        if (creditDestinations == null)
            creditDestinations = new ArrayList<>();

        return creditDestinations;
    }
}
