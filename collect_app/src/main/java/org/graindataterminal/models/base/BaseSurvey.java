package org.graindataterminal.models.base;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.gson.annotations.SerializedName;

import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaField;
import org.graindataterminal.models.zambia.ZambiaField;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseSurvey implements Cloneable {
    public final static int SURVEY_STATE_NEW = 1;
    public final static int SURVEY_STATE_SUBMITTED = 2;
    public final static int SURVEY_STATE_SYNC_ERROR = 3;

    public final static int SURVEY_READ_MODE = 0;
    public final static int SURVEY_EDIT_MODE = 1;

    public final static int SURVEY_TYPE_NONE = 0;
    public final static int SURVEY_TYPE_ZAMBIA = 1;
    public final static int SURVEY_TYPE_TUNISIA = 2;
    public final static int SURVEY_TYPE_SENEGAL = 3;
    public final static int SURVEY_TYPE_CAMEROON = 4;
    public final static int SURVEY_TYPE_GAMBIA = 5;

    public final static String[] SURVEY_VERSION_NONE = {"none"};
    public final static String[] SURVEY_VERSION_ZAMBIA = {"za20150821", "za20151210"};
    public final static String[] SURVEY_VERSION_TUNISIA = {"tn20150916"};
    public final static String[] SURVEY_VERSION_SENEGAL = {"sn20150916", "sn20151111", "sn20151130"};
    public final static String[] SURVEY_VERSION_CAMEROON = {"cm20160108", "cm20160127"};

    @SerializedName("id")
    protected String id;

    @SerializedName("key")
    protected Integer key;

    @SerializedName("mode")
    protected int mode;

    @SerializedName("state")
    protected int state;

    @SerializedName("q_version")
    protected String surveyVersion;

    @SerializedName("app_version")
    protected String appVersion;

    @SerializedName("start_time")
    protected String startTime;

    @SerializedName("end_time")
    protected String endTime;

    @SerializedName("update_time")
    private String updateTime;

    @SerializedName("deviceid")
    protected String deviceId;

    @SerializedName("subscriberid")
    protected String subscriberId;

    @SerializedName("imei")
    protected String imei;

    @SerializedName("sim_serial_number")
    protected String simSerialNumber;

    @SerializedName("phonenumber")
    protected String phoneNumber;

    @SerializedName("field_center_gps")
    protected Map<String, Double> fieldCenterGPS;

    @SerializedName("interviewer")
    protected String interviewerName;

    @SerializedName("village_name")
    protected String villageName;

    @SerializedName("head_name")
    protected String headName;

    @SerializedName("farmer_name")
    protected String farmerName;

    @SerializedName("farmer_gender")
    protected String farmerGender;

    @SerializedName("farmer_birthdate")
    protected String farmerBirthdate;

    @SerializedName("contact_phone")
    protected String contactPhone;

    @SerializedName("farmer_photo_key")
    protected Integer farmerPhotoKey;

    @SerializedName("farmer_photo")
    protected String farmerPhoto;

    @SerializedName("crop_production")
    protected String cropProduction;

    @SerializedName("livestock_production")
    protected String livestockProduction;

    @SerializedName("farm_fishing")
    protected String farmFishing;

    @SerializedName("crop_production_last")
    protected String cropProductionLast;

    @SerializedName("livestock_production_last")
    protected String livestockProductionLast;

    @SerializedName("farm_fishing_last")
    protected String farmFishingLast;

    @SerializedName("source_livelihood")
    private String sourceLivelihood;

    @SerializedName("source_labour")
    private String sourceLabour;

    @SerializedName("level_skills")
    private String levelSkills;

    @SerializedName("education_information_source")
    private List<String> informationSource;

    public BaseSurvey() {
        setDeviceInfo();
    }

    public BaseSurvey clone () {
        try {
            return (BaseSurvey) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setSurveyVersion(String surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public String getSurveyVersion() {
        return surveyVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setFarmerPhotoKey(Integer farmerPhotoKey) {
        this.farmerPhotoKey = farmerPhotoKey;
    }

    public Integer getFarmerPhotoKey() {
        return farmerPhotoKey;
    }

    public void setFarmerPhoto(String farmerPhoto) {
        if (this instanceof SenegalSurvey)
            ((SenegalSurvey) this).setOperatingOfficerImage(farmerPhoto);
        else
            this.farmerPhoto = farmerPhoto;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getHeadName() {
        return headName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerGender(String farmerGender) {
        this.farmerGender = farmerGender;
    }

    public String getFarmerGender() {
        return farmerGender;
    }

    public void setFarmerBirthdate(String farmerBirthdate) {
        this.farmerBirthdate = farmerBirthdate;
    }

    public String getFarmerBirthdate() {
        return farmerBirthdate;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getFarmerPhoto() {
        if (this instanceof SenegalSurvey)
            return ((SenegalSurvey) this).getOperatingOfficerImage();
        else
            return farmerPhoto;
    }

    public void setCropProduction(String cropProduction) {
        this.cropProduction = cropProduction;

        if (cropProduction == null || BaseCrop.answerIdList[1].equals(cropProduction)) {
            if (!Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(surveyVersion)) {
                List<BaseField> baseFields = getFields();
                for (BaseField baseField: baseFields) {
                    if (baseField instanceof ZambiaField)
                        ((ZambiaField) baseField).setCrop(null);
                    else if (baseField instanceof TunisiaField)
                        ((TunisiaField) baseField).setCrop(null);
                }
            }
        }
    }

    public String getCropProduction() {
        return cropProduction;
    }

    public void setLivestockProduction(String livestockProduction) {
        this.livestockProduction = livestockProduction;
    }

    public String getLivestockProduction() {
        return livestockProduction;
    }

    public void setFarmFishing(String farmFishing) {
        this.farmFishing = farmFishing;
    }

    public String getFarmFishing() {
        return farmFishing;
    }

    public void setCropProductionLast(String cropProductionLast) {
        this.cropProductionLast = cropProductionLast;
    }

    public String getCropProductionLast() {
        return cropProductionLast;
    }

    public void setLivestockProductionLast(String livestockProductionLast) {
        this.livestockProductionLast = livestockProductionLast;
    }

    public String getLivestockProductionLast() {
        return livestockProductionLast;
    }

    public void setFarmFishingLast(String farmFishingLast) {
        this.farmFishingLast = farmFishingLast;
    }

    public String getFarmFishingLast() {
        return farmFishingLast;
    }

    public void setSourceLivelihood(String sourceLivelihood) {
        this.sourceLivelihood = sourceLivelihood;
    }

    public String getSourceLivelihood() {
        return sourceLivelihood;
    }

    public void setSourceLabour(String sourceLabour) {
        this.sourceLabour = sourceLabour;
    }

    public String getSourceLabour() {
        return sourceLabour;
    }

    public void setLevelSkills(String levelSkills) {
        this.levelSkills = levelSkills;
    }

    public String getLevelSkills() {
        return levelSkills;
    }

    public void setInformationSource(List<String> informationSource) {
        this.informationSource = informationSource;
    }

    public List<String> getInformationSource() {
        if (informationSource == null)
            informationSource = new ArrayList<>();

        return informationSource;
    }

    public void setFieldCenterGPS(Map<String, Double> fieldCenterGPS) {
        this.fieldCenterGPS = fieldCenterGPS;
    }

    public Map<String, Double> getFieldCenterGPS() {
        if (fieldCenterGPS == null)
            fieldCenterGPS = new HashMap<>();

        return fieldCenterGPS;
    }

    public abstract List getFields();

    protected void setDeviceInfo () {
        Context context = Collect.getInstance().getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        setId(UUID.randomUUID().toString());
        setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        setSubscriberId(telephonyManager.getSubscriberId());
        setImei(telephonyManager.getDeviceId());
        setSimSerialNumber(telephonyManager.getSimSerialNumber());
        setPhoneNumber(telephonyManager.getLine1Number());
    }
}
