package org.graindataterminal.models.cameroon;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class FarmerSustainability implements Cloneable {
    public final static String[] cppProtectiveGearIdList = {"no_not_really", "yes_f", "yes_gl", "yes_masks", "yes_combination"};

    @SerializedName("pro_cpp")
    private String protectiveGear;

    @SerializedName("ppe_used")
    private String mainReason;

    public FarmerSustainability clone () {
        try {
            return (FarmerSustainability) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setProtectiveGear(String protectiveGear) {
        this.protectiveGear = protectiveGear;

        if (TextUtils.isEmpty(protectiveGear) || !cppProtectiveGearIdList[0].equals(protectiveGear))
            setMainReason(null);
    }

    public String getProtectiveGear() {
        return protectiveGear;
    }

    public void setMainReason(String mainReason) {
        this.mainReason = mainReason;
    }

    public String getMainReason() {
        return mainReason;
    }
}
