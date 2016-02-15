package org.graindataterminal.models.cameroon;

import com.google.gson.annotations.SerializedName;

public class FarmerFarmScreening implements Cloneable {
    @SerializedName("coca")
    private String cocoa;

    @SerializedName("yield")
    private String yield;

    @SerializedName("others_crops")
    private String otherCrops;

    public FarmerFarmScreening clone () {
        try {
            return (FarmerFarmScreening) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setCocoa(String cocoa) {
        this.cocoa = cocoa;
    }

    public String getCocoa() {
        return cocoa;
    }

    public void setYield(String yield) {
        this.yield = yield;
    }

    public String getYield() {
        return yield;
    }

    public void setOtherCrops(String otherCrops) {
        this.otherCrops = otherCrops;
    }

    public String getOtherCrops() {
        return otherCrops;
    }
}
