package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommercialGroup implements Cloneable {
    public final static String[] marketingMethodIdList = {"bord_champ", "vente_groupe", "vente_directe", "commision", "contractualisation", "exportation", "autre_mode_commercialisation"};
    public final static String[] marketingConstraintIdList = {"aucune_contrainte", "forte_disponibilite", "conservation", "conditionnement", "transport", "enclavement", "autre_contrainte"};

    @SerializedName("commerce_production")
    private List<String> tradeProduction;

    @SerializedName("atr_cm_pr")
    private String otherTradeProduction;

    @SerializedName("contrainte_commerciale")
    private List<String> commercialConstraints;

    @SerializedName("atr_contr_com")
    private String otherCommercialConstraints;

    public CommercialGroup clone () {
        try {
            return (CommercialGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setTradeProduction(List<String> tradeProduction) {
        this.tradeProduction = tradeProduction;

        if (tradeProduction == null || !tradeProduction.contains(marketingMethodIdList[marketingMethodIdList.length - 1]))
            setOtherTradeProduction(null);
    }

    public List<String> getTradeProduction() {
        if (tradeProduction == null)
            tradeProduction = new ArrayList<>();

        return tradeProduction;
    }

    public void setOtherTradeProduction(String otherTradeProduction) {
        this.otherTradeProduction = otherTradeProduction;
    }

    public String getOtherTradeProduction() {
        return otherTradeProduction;
    }

    public void setCommercialConstraints(List<String> commercialConstraints) {
        this.commercialConstraints = commercialConstraints;

        if (commercialConstraints == null || !commercialConstraints.contains(marketingConstraintIdList[marketingConstraintIdList.length - 1]))
            setOtherTradeProduction(null);
    }

    public List<String> getCommercialConstraints() {
        if (commercialConstraints == null)
            commercialConstraints = new ArrayList<>();

        return commercialConstraints;
    }

    public void setOtherCommercialConstraints(String otherCommercialConstraints) {
        this.otherCommercialConstraints = otherCommercialConstraints;
    }

    public String getOtherCommercialConstraints() {
        return otherCommercialConstraints;
    }
}
