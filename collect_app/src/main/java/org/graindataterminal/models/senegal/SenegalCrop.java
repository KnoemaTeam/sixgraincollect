package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseCrop;

import java.util.ArrayList;
import java.util.List;

public class SenegalCrop extends BaseCrop {
    @SerializedName("crop")
    private SenegalCropGroup cropGroup;

    @SerializedName("attaque")
    private SenegalCropAttackGroup attackGroup;

    public SenegalCrop() {
        super();
    }

    public SenegalCrop clone () {
        return (SenegalCrop) super.clone();
    }

    public void setCropGroup(SenegalCropGroup cropGroup) {
        this.cropGroup = cropGroup;
    }

    public SenegalCropGroup getCropGroup() {
        if (cropGroup == null)
            cropGroup = new SenegalCropGroup();

        return cropGroup;
    }

    public void setAttackGroup(SenegalCropAttackGroup attackGroup) {
        this.attackGroup = attackGroup;
    }

    public SenegalCropAttackGroup getAttackGroup() {
        if (attackGroup == null)
            attackGroup = new SenegalCropAttackGroup();

        return attackGroup;
    }
}
