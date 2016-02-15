package org.graindataterminal.models.tunisia;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseSurvey;

import java.util.ArrayList;
import java.util.List;

public class TunisiaSurvey extends BaseSurvey implements Cloneable {
    @SerializedName("reg_card_number")
    private String regCardNumber;

    @SerializedName("governorate")
    private String governorate;

    @SerializedName("delegation")
    private String delegation;

    @SerializedName("field_repeat")
    private List<TunisiaField> fields;

    public TunisiaSurvey() {
        super();
    }

    public TunisiaSurvey clone () {
        return (TunisiaSurvey) super.clone();
    }

    public void setRegCardNumber(String regCardNumber) {
        this.regCardNumber = regCardNumber;
    }

    public String getRegCardNumber() {
        return regCardNumber;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setFields(List<TunisiaField> fields) {
        this.fields = fields;
    }

    @Override
    public List<TunisiaField> getFields() {
        if (fields == null)
            fields = new ArrayList<>();

        return fields;
    }
}
