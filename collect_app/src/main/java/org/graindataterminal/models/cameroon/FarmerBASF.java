package org.graindataterminal.models.cameroon;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class FarmerBASF implements Cloneable {
    @SerializedName("know_basf")
    private String isBASFKnown;

    @SerializedName("basf_product")
    private String BASFproducts;

    public FarmerBASF clone () {
        try {
            return (FarmerBASF) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setIsBASFKnown(String isBASFKnown) {
        this.isBASFKnown = isBASFKnown;

        if (TextUtils.isEmpty(isBASFKnown) || !CameroonSurvey.answerIdList[0].equals(isBASFKnown))
            setBASFproducts(null);
    }

    public String getIsBASFKnown() {
        return isBASFKnown;
    }

    public void setBASFproducts(String BASFproducts) {
        this.BASFproducts = BASFproducts;
    }

    public String getBASFproducts() {
        return BASFproducts;
    }
}
