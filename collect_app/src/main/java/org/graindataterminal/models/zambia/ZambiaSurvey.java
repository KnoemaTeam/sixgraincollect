package org.graindataterminal.models.zambia;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseSurvey;

import java.util.ArrayList;
import java.util.List;

public class ZambiaSurvey extends BaseSurvey implements Cloneable {
    @SerializedName("block_name")
    private String blockName;

    @SerializedName("vet_camp_name")
    private String vetCampName;

    @SerializedName("agr_camp_name")
    private String agrCampName;

    @SerializedName("field_repeat")
    private List<ZambiaField> fields;

    public ZambiaSurvey() {
        super();
    }

    public ZambiaSurvey clone () {
        return (ZambiaSurvey) super.clone();
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setVetCampName(String vetCampName) {
        this.vetCampName = vetCampName;
    }

    public String getVetCampName() {
        return vetCampName;
    }

    public void setAgrCampName(String agrCampName) {
        this.agrCampName = agrCampName;
    }

    public String getAgrCampName() {
        return agrCampName;
    }

    public void setFields(List<ZambiaField> fields) {
        this.fields = fields;
    }

    @Override
    public List<ZambiaField> getFields() {
        if (fields == null)
            fields = new ArrayList<>();

        return fields;
    }
}
