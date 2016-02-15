package org.graindataterminal.models.zambia;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseField;

public class ZambiaField extends BaseField {

    @SerializedName("crop")
    protected ZambiaCrop crop;

    public ZambiaField() {
        super();
    }

    public ZambiaField clone () {
        return (ZambiaField) super.clone();
    }

    public void setCrop(ZambiaCrop crop) {
        this.crop = crop;
    }

    @Override
    public ZambiaCrop getCrop() {
        return crop;
    }
}
