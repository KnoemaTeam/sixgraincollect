package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

public class FarmOwner implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("name_espece")
    private String name;

    @SerializedName("effectif")
    private String employeesCount;

    public FarmOwner() {

    }

    public FarmOwner clone () {
        try {
            return (FarmOwner) super.clone();
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

    public void setEmployeesCount(String employeesCount) {
        this.employeesCount = employeesCount;
    }

    public String getEmployeesCount() {
        return employeesCount;
    }
}
