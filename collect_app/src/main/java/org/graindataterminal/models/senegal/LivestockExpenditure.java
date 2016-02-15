package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class LivestockExpenditure implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("depense_elevage")
    private String expenseType;

    @SerializedName("montant")
    private String amount;

    public LivestockExpenditure() {

    }

    public LivestockExpenditure clone () {
        try {
            return (LivestockExpenditure) super.clone();
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

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}
