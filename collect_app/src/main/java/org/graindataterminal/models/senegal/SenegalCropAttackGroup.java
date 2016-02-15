package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class SenegalCropAttackGroup implements Cloneable {
    @SerializedName("spec_attaque")
    private String verminAttacksType;

    @SerializedName("frequence_attaque")
    private String verminAttacksDate;

    public SenegalCropAttackGroup clone () {
        try {
            return (SenegalCropAttackGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setVerminAttacksType(String verminAttacksType) {
        this.verminAttacksType = verminAttacksType;
    }

    public String getVerminAttacksType() {
        return verminAttacksType;
    }

    public void setVerminAttacksDate(String verminAttacksDate) {
        this.verminAttacksDate = verminAttacksDate;
    }

    public String getVerminAttacksDate() {
        return verminAttacksDate;
    }
}
