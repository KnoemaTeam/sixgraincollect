package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class LivestockProduction implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("name_product")
    private String name;

    @SerializedName("production_produit")
    private String productAvg;

    public LivestockProduction() {

    }

    public LivestockProduction clone () {
        try {
            return (LivestockProduction) super.clone();
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

    public void setProductAvg(String productAvg) {
        this.productAvg = productAvg;
    }

    public String getProductAvg() {
        return productAvg;
    }
}
