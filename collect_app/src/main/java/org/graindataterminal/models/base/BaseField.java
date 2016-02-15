package org.graindataterminal.models.base;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseField implements Cloneable {
    @SerializedName("title")
    protected String title;

    @SerializedName("non_field")
    protected String isField;

    @SerializedName("non_field_type")
    protected String fieldType;

    @SerializedName("field_photo_key")
    protected Integer fieldPhotoKey;

    @SerializedName("field_photo")
    protected String fieldPhoto;

    @SerializedName("corners")
    protected List<Map<String, Double>> corners;

    public BaseField() {}

    public BaseField clone () {
        try {
            return (BaseField) super.clone();
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
        if (title == null) {
            title = getCrop() != null ? getCrop().getTitle() : null;
        }

        return title;
    }

    public void setIsField(String isField) {
        this.isField = isField;

        if (BaseCrop.answerIdList[1].equals(isField)) {
            setTitle(null);
            setFieldType(null);
        }
    }

    public String getIsField() {
        return isField;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldPhotoKey(Integer fieldPhotoKey) {
        this.fieldPhotoKey = fieldPhotoKey;
    }

    public Integer getFieldPhotoKey() {
        return fieldPhotoKey;
    }

    public void setFieldPhoto(String fieldPhoto) {
        this.fieldPhoto = fieldPhoto;
    }

    public String getFieldPhoto() {
        return fieldPhoto;
    }

    public abstract BaseCrop getCrop();

    public void setCorners(List<Map<String, Double>> corners) {
        this.corners = corners;
    }

    public List<Map<String, Double>> getCorners() {
        if (corners == null)
            corners = new ArrayList<>();

        return corners;
    }


}
