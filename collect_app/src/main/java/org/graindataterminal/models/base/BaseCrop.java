package org.graindataterminal.models.base;

import com.google.gson.annotations.SerializedName;

public abstract class BaseCrop implements Cloneable {
    public static String[] answerIdList = {"yes", "no"};

    @SerializedName("title")
    protected String title;

    @SerializedName("crop_name")
    protected String cropName;

    @SerializedName("crop_photo_key")
    protected Integer cropPhotoKey;

    @SerializedName("crop_photo")
    protected String cropPhoto;

    @SerializedName("area")
    protected String cropArea;

    @SerializedName("area_unit")
    protected String areaUnit;

    @SerializedName("crop_name_last")
    protected String cropNameLast;

    @SerializedName("area_last")
    protected String cropAreaLast;

    @SerializedName("area_unit_last")
    protected String areaUnitLast;

    @SerializedName("water_source")
    protected String waterSource;

    @SerializedName("irrigation_source")
    protected String irrigationSource;

    @SerializedName("main_motive")
    protected String mainMotive;

    @SerializedName("seed_sown")
    protected String seedSown;

    @SerializedName("seed_purchase")
    protected String seedPurchase;

    @SerializedName("seed_name")
    protected String seedName;

    @SerializedName("seed_cost")
    protected String seedCost;

    @SerializedName("fertilizer_use")
    protected String isFertilizerUsed;

    @SerializedName("fertilizer_amount")
    protected String fertilizerAmount;

    @SerializedName("fertilizer_cost")
    protected String fertilizerCost;

    @SerializedName("pesticide_use")
    protected String isPesticideUsed;

    @SerializedName("harvested_quantity")
    protected String harvestedQuantity;

    @SerializedName("harvested_unit")
    protected String harvestedUnit;

    public BaseCrop () {}

    public BaseCrop clone () {
        try {
            return (BaseCrop) super.clone();
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

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropPhotoKey(Integer cropPhotoKey) {
        this.cropPhotoKey = cropPhotoKey;
    }

    public Integer getCropPhotoKey() {
        return cropPhotoKey;
    }

    public void setCropPhoto(String cropPhoto) {
        this.cropPhoto = cropPhoto;
    }

    public String getCropPhoto() {
        return cropPhoto;
    }

    public void setCropArea(String cropArea) {
        this.cropArea = cropArea;
    }

    public String getCropArea() {
        return cropArea;
    }

    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public void setCropNameLast(String cropNameLast) {
        this.cropNameLast = cropNameLast;
    }

    public String getCropNameLast() {
        return cropNameLast;
    }

    public void setCropAreaLast(String cropAreaLast) {
        this.cropAreaLast = cropAreaLast;
    }

    public String getCropAreaLast() {
        return cropAreaLast;
    }

    public void setAreaUnitLast(String areaUnitLast) {
        this.areaUnitLast = areaUnitLast;
    }

    public String getAreaUnitLast() {
        return areaUnitLast;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setIrrigationSource(String irrigationSource) {
        this.irrigationSource = irrigationSource;
    }

    public String getIrrigationSource() {
        return irrigationSource;
    }

    public void setMainMotive(String mainMotive) {
        this.mainMotive = mainMotive;
    }

    public String getMainMotive() {
        return mainMotive;
    }

    public void setSeedSown(String seedSown) {
        this.seedSown = seedSown;
    }

    public String getSeedSown() {
        return seedSown;
    }

    public void setSeedPurchase(String seedPurchase) {
        this.seedPurchase = seedPurchase;

        if (answerIdList[1].equals(seedPurchase)) {
            setSeedName(null);
            setSeedCost(null);
        }
    }

    public String getSeedPurchase() {
        return seedPurchase;
    }

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }

    public String getSeedName() {
        return seedName;
    }

    public void setSeedCost(String seedCost) {
        this.seedCost = seedCost;
    }

    public String getSeedCost() {
        return seedCost;
    }

    public void setIsFertilizerUsed(String isFertilizerUsed) {
        this.isFertilizerUsed = isFertilizerUsed;

        if (isFertilizerUsed == null || answerIdList[1].equals(isFertilizerUsed)) {
            setFertilizerAmount(null);
            setFertilizerCost(null);
        }
    }

    public String getIsFertilizerUsed() {
        return isFertilizerUsed;
    }

    public void setFertilizerAmount(String fertilizerAmount) {
        this.fertilizerAmount = fertilizerAmount;
    }

    public String getFertilizerAmount() {
        return fertilizerAmount;
    }

    public void setFertilizerCost(String fertilizerCost) {
        this.fertilizerCost = fertilizerCost;
    }

    public String getFertilizerCost() {
        return fertilizerCost;
    }

    public void setIsPesticideUsed(String isPesticideUsed) {
        this.isPesticideUsed = isPesticideUsed;
    }

    public String getIsPesticideUsed() {
        return isPesticideUsed;
    }

    public void setHarvestedQuantity(String harvestedQuantity) {
        this.harvestedQuantity = harvestedQuantity;
    }

    public String getHarvestedQuantity() {
        return harvestedQuantity;
    }

    public void setHarvestedUnit(String harvestedUnit) {
        this.harvestedUnit = harvestedUnit;
    }

    public String getHarvestedUnit() {
        return harvestedUnit;
    }
}
