package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class Expenses implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("nature")
    private String nature;

    @SerializedName("montant_depense")
    private String amount;

    public Expenses() {

    }

    public Expenses clone () {
        try {
            return (Expenses) super.clone();
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

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getNature() {
        return nature;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}
