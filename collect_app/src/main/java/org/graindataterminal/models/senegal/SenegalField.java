package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseField;

import java.util.ArrayList;
import java.util.List;

public class SenegalField extends BaseField implements Cloneable {
    @SerializedName("use_engrais")
    private String isFertilizerUsed;

    @SerializedName("input")
    private List<String> fertilizerType;

    @SerializedName("qte_engrais")
    private String fertilizerAmount;

    @SerializedName("acqu_engrais")
    private String fertilizerMode;

    @SerializedName("cout_engrais")
    private String fertilizerCost;

    @SerializedName("use_pesticide")
    private String isPesticideUsed;

    @SerializedName("cout_pesticide")
    private String pesticideCost;

    @SerializedName("use_pratique_restauration_sol")
    private String isSoilRestorationUsed;

    @SerializedName("restauration")
    private List<String> soilRestorationMethods;

    @SerializedName("cout_restauration")
    private String soilRestorationCost;

    @SerializedName("speculation")
    protected List<SenegalCrop> cropList;

    public SenegalField() {
        super();
    }

    public SenegalField clone () {
        return (SenegalField) super.clone();
    }

    public void setIsFertilizerUsed(String isFertilizerUsed) {
        this.isFertilizerUsed = isFertilizerUsed;

        if (isFertilizerUsed == null || SenegalSurvey.answerIdList[1].equals(isFertilizerUsed)) {
            setFertilizerType(null);
            setFertilizerAmount(null);
            setFertilizerMode(null);
            setFertilizerCost(null);
        }
    }

    public String getIsFertilizerUsed() {
        return isFertilizerUsed;
    }

    public void setFertilizerType(List<String> fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public List<String> getFertilizerType() {
        if (fertilizerType == null)
            fertilizerType = new ArrayList<>();

        return fertilizerType;
    }

    public void setFertilizerAmount(String fertilizerAmount) {
        this.fertilizerAmount = fertilizerAmount;
    }

    public String getFertilizerAmount() {
        return fertilizerAmount;
    }

    public void setFertilizerMode(String fertilizerMode) {
        this.fertilizerMode = fertilizerMode;
    }

    public String getFertilizerMode() {
        return fertilizerMode;
    }

    public void setFertilizerCost(String fertilizerCost) {
        this.fertilizerCost = fertilizerCost;
    }

    public String getFertilizerCost() {
        return fertilizerCost;
    }

    public void setIsPesticideUsed(String isPesticideUsed) {
        this.isPesticideUsed = isPesticideUsed;

        if (isPesticideUsed == null || SenegalSurvey.answerIdList[1].equals(isPesticideUsed))
            setPesticideCost(null);
    }

    public String getIsPesticideUsed() {
        return isPesticideUsed;
    }

    public void setPesticideCost(String pesticideCost) {
        this.pesticideCost = pesticideCost;
    }

    public String getPesticideCost() {
        return pesticideCost;
    }

    public void setIsSoilRestorationUsed(String isSoilRestorationUsed) {
        this.isSoilRestorationUsed = isSoilRestorationUsed;
    }

    public String getIsSoilRestorationUsed() {
        return isSoilRestorationUsed;
    }

    public void setSoilRestorationMethods(List<String> soilRestorationMethods) {
        this.soilRestorationMethods = soilRestorationMethods;
    }

    public List<String> getSoilRestorationMethods() {
        if (soilRestorationMethods == null)
            soilRestorationMethods = new ArrayList<>();

        return soilRestorationMethods;
    }

    public void setSoilRestorationCost(String soilRestorationCost) {
        this.soilRestorationCost = soilRestorationCost;
    }

    public String getSoilRestorationCost() {
        return soilRestorationCost;
    }

    @Override
    public BaseCrop getCrop() {
        return null;
    }

    public void setCropList(List<SenegalCrop> cropList) {
        this.cropList = cropList;
    }

    public List<SenegalCrop> getCropList() {
        if (cropList == null)
            cropList = new ArrayList<>();

        return cropList;
    }
}
