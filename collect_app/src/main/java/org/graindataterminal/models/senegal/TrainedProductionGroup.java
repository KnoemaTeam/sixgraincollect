package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrainedProductionGroup implements Cloneable {
    public final static String[] productionIdList = {"naisseur", "embouche", "production_lait", "production_oeuf", "chevaux_course", "animaux_trait", "production_miel"};

    @SerializedName("type_prod")
    private List<String> trainedProductionTypes;

    @SerializedName("prod_elevage_naisseur")
    private List<String> livestockProducers;

    @SerializedName("pr_viande")
    private List<String> meatProductions;

    @SerializedName("pr_lait")
    private List<String> milkProductions;

    @SerializedName("pr_an_trait")
    private List<String> draughtAnimals;

    public TrainedProductionGroup clone () {
        try {
            return (TrainedProductionGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setTrainedProductionTypes(List<String> trainedProductionTypes) {
        this.trainedProductionTypes = trainedProductionTypes;

        if (trainedProductionTypes == null || trainedProductionTypes.isEmpty()) {
            setLivestockProducers(null);
            setMeatProductions(null);
            setMilkProductions(null);
            setDraughtAnimals(null);
        }
        else {
            if (!trainedProductionTypes.contains(productionIdList[0]))
                setLivestockProducers(null);
            else if (!trainedProductionTypes.contains(productionIdList[1]))
                setMeatProductions(null);
            else if (!trainedProductionTypes.contains(productionIdList[2]))
                setMilkProductions(null);
            else if (!trainedProductionTypes.contains(productionIdList[productionIdList.length - 2]))
                setDraughtAnimals(null);
        }
    }

    public List<String> getTrainedProductionTypes() {
        if (trainedProductionTypes == null)
            trainedProductionTypes = new ArrayList<>();

        return trainedProductionTypes;
    }

    public void setLivestockProducers(List<String> livestockProducers) {
        this.livestockProducers = livestockProducers;
    }

    public List<String> getLivestockProducers() {
        if (livestockProducers == null)
            livestockProducers = new ArrayList<>();

        return livestockProducers;
    }

    public void setMeatProductions(List<String> meatProductions) {
        this.meatProductions = meatProductions;
    }

    public List<String> getMeatProductions() {
        if (meatProductions == null)
            meatProductions = new ArrayList<>();

        return meatProductions;
    }

    public void setMilkProductions(List<String> milkProductions) {
        this.milkProductions = milkProductions;
    }

    public List<String> getMilkProductions() {
        if (milkProductions == null)
            milkProductions = new ArrayList<>();

        return milkProductions;
    }

    public void setDraughtAnimals(List<String> draughtAnimals) {
        this.draughtAnimals = draughtAnimals;
    }

    public List<String> getDraughtAnimals() {
        if (draughtAnimals == null)
            draughtAnimals = new ArrayList<>();

        return draughtAnimals;
    }
}
