package org.graindataterminal.models.cameroon;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseSurvey;

import java.util.List;

public class CameroonSurvey extends BaseSurvey {
    public static String[] answerIdList = {"yes", "no"};

    @SerializedName("general")
    private FarmerGeneralInfo farmerGeneralInfo;

    @SerializedName("farm_screening")
    private FarmerFarmScreening farmerFarmScreening;

    @SerializedName("farm_practice")
    private FarmerFarmPractice farmerFarmPractice;

    @SerializedName("basf")
    private FarmerBASF farmerBASF;

    @SerializedName("cpp_orders")
    private FarmerCPPOrders farmerCPPOrders;

    @SerializedName("sustainability")
    private FarmerSustainability farmerSustainability;

    public CameroonSurvey() {
        super();
    }

    public void setFarmerGeneralInfo(FarmerGeneralInfo farmerGeneralInfo) {
        this.farmerGeneralInfo = farmerGeneralInfo;
    }

    public FarmerGeneralInfo getFarmerGeneralInfo() {
        if (farmerGeneralInfo == null)
            farmerGeneralInfo = new FarmerGeneralInfo();

        return farmerGeneralInfo;
    }

    public void setFarmerFarmScreening(FarmerFarmScreening farmerFarmScreening) {
        this.farmerFarmScreening = farmerFarmScreening;
    }

    public FarmerFarmScreening getFarmerFarmScreening() {
        if (farmerFarmScreening == null)
            farmerFarmScreening = new FarmerFarmScreening();

        return farmerFarmScreening;
    }

    public void setFarmerFarmPractice(FarmerFarmPractice farmerFarmPractice) {
        this.farmerFarmPractice = farmerFarmPractice;
    }

    public FarmerFarmPractice getFarmerFarmPractice() {
        if (farmerFarmPractice == null)
            farmerFarmPractice = new FarmerFarmPractice();

        return farmerFarmPractice;
    }

    public void setFarmerBASF(FarmerBASF farmerBASF) {
        this.farmerBASF = farmerBASF;
    }

    public FarmerBASF getFarmerBASF() {
        if (farmerBASF == null)
            farmerBASF = new FarmerBASF();

        return farmerBASF;
    }

    public void setFarmerCPPOrders(FarmerCPPOrders farmerCPPOrders) {
        this.farmerCPPOrders = farmerCPPOrders;
    }

    public FarmerCPPOrders getFarmerCPPOrders() {
        if (farmerCPPOrders == null)
            farmerCPPOrders = new FarmerCPPOrders();

        return farmerCPPOrders;
    }

    public void setFarmerSustainability(FarmerSustainability farmerSustainability) {
        this.farmerSustainability = farmerSustainability;
    }

    public FarmerSustainability getFarmerSustainability() {
        if (farmerSustainability == null)
            farmerSustainability = new FarmerSustainability();

        return farmerSustainability;
    }

    @Override
    public List getFields() {
        return null;
    }
}
