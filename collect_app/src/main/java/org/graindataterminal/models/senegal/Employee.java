package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class Employee implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("qualification_name")
    private String qualificationName;

    @SerializedName("nombre_total")
    private String headWorkforceCount;

    @SerializedName("nomre_homme")
    private String maleWorkforceCount;

    @SerializedName("nombre_femme")
    private String femaleWorkforceCount;

    @SerializedName("nombre_permanent")
    private String permanentWorkforceCount;

    @SerializedName("nombre_saisonnier")
    private String seasonalWorkforceCount;

    public Employee() {

    }

    public Employee clone () {
        try {
            return (Employee) super.clone();
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

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public void setHeadWorkforceCount(String headWorkforceCount) {
        this.headWorkforceCount = headWorkforceCount;
    }

    public String getHeadWorkforceCount() {
        return headWorkforceCount;
    }

    public void setMaleWorkforceCount(String maleWorkforceCount) {
        this.maleWorkforceCount = maleWorkforceCount;
    }

    public String getMaleWorkforceCount() {
        return maleWorkforceCount;
    }

    public void setFemaleWorkforceCount(String femaleWorkforceCount) {
        this.femaleWorkforceCount = femaleWorkforceCount;
    }

    public String getFemaleWorkforceCount() {
        return femaleWorkforceCount;
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
