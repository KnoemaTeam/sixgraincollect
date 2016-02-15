package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class FarmingPracticeGroup implements Cloneable {
    @SerializedName("fait_elevage")
    private String isMadeFarming;

    public FarmingPracticeGroup clone () {
        try {
            return (FarmingPracticeGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setIsMadeFarming(String isMadeFarming) {
        this.isMadeFarming = isMadeFarming;
    }

    public String getIsMadeFarming() {
        return isMadeFarming;
    }
}
