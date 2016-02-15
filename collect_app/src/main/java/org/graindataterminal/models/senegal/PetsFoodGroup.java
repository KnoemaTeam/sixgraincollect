package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PetsFoodGroup implements Cloneable {
    public final static String[] modeAlimentationIdList = {"paturage_alimentation", "auge", "autre_alimentation"};

    @SerializedName("md_aliment")
    private List<String> foodFeed;

    @SerializedName("ali_pat")
    private List<String> foodDriving;

    @SerializedName("ali_auge")
    private List<String> foodTrough;

    public PetsFoodGroup clone () {
        try {
            return (PetsFoodGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setFoodFeed(List<String> foodFeed) {
        this.foodFeed = foodFeed;

        if (foodFeed == null || foodFeed.isEmpty()) {
            setFoodDriving(null);
            setFoodTrough(null);
        }
        else {
            if (!foodFeed.contains(modeAlimentationIdList[0]))
                setFoodDriving(null);
            else if (!foodFeed.contains(modeAlimentationIdList[1]))
                setFoodTrough(null);
        }
    }

    public List<String> getFoodFeed() {
        if (foodFeed == null)
            foodFeed = new ArrayList<>();

        return foodFeed;
    }

    public void setFoodDriving(List<String> foodDriving) {
        this.foodDriving = foodDriving;
    }

    public List<String> getFoodDriving() {
        if (foodDriving == null)
            foodDriving = new ArrayList<>();

        return foodDriving;
    }

    public void setFoodTrough(List<String> foodTrough) {
        this.foodTrough = foodTrough;
    }

    public List<String> getFoodTrough() {
        if (foodTrough == null)
            foodTrough = new ArrayList<>();

        return foodTrough;
    }
}
