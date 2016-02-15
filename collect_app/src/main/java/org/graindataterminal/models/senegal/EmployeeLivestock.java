package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class EmployeeLivestock implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("spec")
    private String employSpecialty;

    @SerializedName("total_effectif")
    private String totalWorkforceCount;

    @SerializedName("homme_elevage")
    private String maleSpecialteWorkforceCount;

    @SerializedName("femme_elevage")
    private String femaleSpecialteWorkforceCount;

    @SerializedName("permanent_elevage")
    private String permanentWorkforceCount;

    @SerializedName("saisonnier_elevage")
    private String seasonalWorkforceCount;

    public EmployeeLivestock() {

    }

    public EmployeeLivestock clone () {
        try {
            return (EmployeeLivestock) super.clone();
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

    public void setEmploySpecialty(String employSpecialty) {
        this.employSpecialty = employSpecialty;
    }

    public String getEmploySpecialty() {
        return employSpecialty;
    }

    public void setTotalWorkforceCount(String totalWorkforceCount) {
        this.totalWorkforceCount = totalWorkforceCount;
    }

    public String getTotalWorkforceCount() {
        return totalWorkforceCount;
    }

    public void setMaleSpecialteWorkforceCount(String maleSpecialteWorkforceCount) {
        this.maleSpecialteWorkforceCount = maleSpecialteWorkforceCount;
    }

    public String getMaleSpecialteWorkforceCount() {
        return maleSpecialteWorkforceCount;
    }

    public void setFemaleSpecialteWorkforceCount(String femaleSpecialteWorkforceCount) {
        this.femaleSpecialteWorkforceCount = femaleSpecialteWorkforceCount;
    }

    public String getFemaleSpecialteWorkforceCount() {
        return femaleSpecialteWorkforceCount;
    }

    public void setPermanentWorkforceCount(String permanentWorkforceCount) {
        this.permanentWorkforceCount = permanentWorkforceCount;
    }

    public String getPermanentWorkforceCount() {
        return permanentWorkforceCount;
    }

    public void setSeasonalWorkforceCount(String seasonalWorkforceCount) {
        this.seasonalWorkforceCount = seasonalWorkforceCount;
    }

    public String getSeasonalWorkforceCount() {
        return seasonalWorkforceCount;
    }
}
