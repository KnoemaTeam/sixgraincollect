package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class OperatingEquipment implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("materiel_name")
    private String name;

    @SerializedName("nombre_materiel")
    private String count;

    @SerializedName("cout_materiel")
    private String cost;

    @SerializedName("annee_acquistion_materiel")
    private String lastShoppingDate;

    @SerializedName("mode_acquisition")
    private String purchaseReason;

    @SerializedName("materiel_sub")
    private String hasSubsidized;

    @SerializedName("etat_materiel")
    private String statePurchase;

    public OperatingEquipment() {

    }

    public OperatingEquipment clone () {
        try {
            return (OperatingEquipment) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }

    public void setLastShoppingDate(String lastShoppingDate) {
        this.lastShoppingDate = lastShoppingDate;
    }

    public String getLastShoppingDate() {
        return lastShoppingDate;
    }

    public void setPurchaseReason(String purchaseReason) {
        this.purchaseReason = purchaseReason;
    }

    public String getPurchaseReason() {
        return purchaseReason;
    }

    public void setHasSubsidized(String hasSubsidized) {
        this.hasSubsidized = hasSubsidized;
    }

    public String getHasSubsidized() {
        return hasSubsidized;
    }

    public void setStatePurchase(String statePurchase) {
        this.statePurchase = statePurchase;
    }

    public String getStatePurchase() {
        return statePurchase;
    }
}
