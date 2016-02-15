package org.graindataterminal.models.tunisia;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseField;

public class TunisiaField extends BaseField {

    @SerializedName("crop")
    protected TunisiaCrop crop;

    public TunisiaField() {
        super();
    }

    public TunisiaField clone () {
        return (TunisiaField) super.clone();
    }

    public void setCrop(TunisiaCrop crop) {
        this.crop = crop;
    }

    @Override
    public TunisiaCrop getCrop() {
        return crop;
    }
}
