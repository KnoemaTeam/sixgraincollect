package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductionDestinationGroup implements Cloneable {
    public final static String[] mainProductionIdList = {"autoconsommation", "generateur_revenu"};

    @SerializedName("dest_prod_viande")
    private List<String> meatDestinationProduction;

    @SerializedName("dest_prod_lait")
    private List<String> milkDestinationProduction;

    @SerializedName("dest_prod_oeuf")
    private List<String> eggsDestinationProduction;

    @SerializedName("dest_prod_miel")
    private List<String> honeyDestinationProduction;

    public ProductionDestinationGroup clone () {
        try {
            return (ProductionDestinationGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setMeatDestinationProduction(List<String> meatDestinationProduction) {
        this.meatDestinationProduction = meatDestinationProduction;
    }

    public List<String> getMeatDestinationProduction() {
        if (meatDestinationProduction == null)
            meatDestinationProduction = new ArrayList<>();

        return meatDestinationProduction;
    }

    public void setMilkDestinationProduction(List<String> milkDestinationProduction) {
        this.milkDestinationProduction = milkDestinationProduction;
    }

    public List<String> getMilkDestinationProduction() {
        if (milkDestinationProduction == null)
            milkDestinationProduction = new ArrayList<>();

        return milkDestinationProduction;
    }

    public void setEggsDestinationProduction(List<String> eggsDestinationProduction) {
        this.eggsDestinationProduction = eggsDestinationProduction;
    }

    public List<String> getEggsDestinationProduction() {
        if (eggsDestinationProduction == null)
            eggsDestinationProduction = new ArrayList<>();

        return eggsDestinationProduction;
    }

    public void setHoneyDestinationProduction(List<String> honeyDestinationProduction) {
        this.honeyDestinationProduction = honeyDestinationProduction;
    }

    public List<String> getHoneyDestinationProduction() {
        if (honeyDestinationProduction == null)
            honeyDestinationProduction = new ArrayList<>();

        return honeyDestinationProduction;
    }
}
