package org.graindataterminal.models.cameroon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FarmerGeneralInfo implements Cloneable {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("date_birth")
    private String birthDate;

    @SerializedName("region")
    private String region;

    @SerializedName("departement")
    private String departement;

    @SerializedName("ass_extention")
    private String extensionOfficer;

    @SerializedName("ass_cooperative")
    private String cooperative;

    @SerializedName("geotag")
    private Map<String, Double> geoLocation;

    @SerializedName("picture")
    private String photo;

    @SerializedName("pref_local_lang")
    private List<String> localLanguage;

    @SerializedName("pref_inter_lang")
    private List<String> interLanguage;

    @SerializedName("education_level")
    private String educationLevel;

    @SerializedName("mobile")
    private String phoneNumber;

    public FarmerGeneralInfo clone () {
        try {
            return (FarmerGeneralInfo) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getDepartement() {
        return departement;
    }

    public void setExtensionOfficer(String extensionOfficer) {
        this.extensionOfficer = extensionOfficer;
    }

    public String getExtensionOfficer() {
        return extensionOfficer;
    }

    public void setCooperative(String cooperative) {
        this.cooperative = cooperative;
    }

    public String getCooperative() {
        return cooperative;
    }

    public void setGeoLocation(Map<String, Double> geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Map<String, Double> getGeoLocation() {
        if (geoLocation == null)
            geoLocation = new HashMap<>();

        return geoLocation;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setLocalLanguage(List<String> localLanguage) {
        this.localLanguage = localLanguage;
    }

    public List<String> getLocalLanguage() {
        if (localLanguage == null)
            localLanguage = new ArrayList<>();

        return localLanguage;
    }

    public void setInterLanguage(List<String> interLanguage) {
        this.interLanguage = interLanguage;
    }

    public List<String> getInterLanguage() {
        if (interLanguage == null)
            interLanguage = new ArrayList<>();

        return interLanguage;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
