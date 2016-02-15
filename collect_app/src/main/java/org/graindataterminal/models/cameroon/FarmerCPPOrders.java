package org.graindataterminal.models.cameroon;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FarmerCPPOrders implements Cloneable {
    @SerializedName("fr_cpp")
    private List<String> cppSourceList;

    @SerializedName("dec_cpp")
    private List<String> cppBuyReasonList;

    @SerializedName("tec_ad")
    private String isAdviceReceived;

    @SerializedName("pro_ad")
    private List<String> adviceSourceList;

    public FarmerCPPOrders clone () {
        try {
            return (FarmerCPPOrders) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setCppSourceList(List<String> cppSourceList) {
        this.cppSourceList = cppSourceList;
    }

    public List<String> getCppSourceList() {
        if (cppSourceList == null)
            cppSourceList = new ArrayList<>();

        return cppSourceList;
    }

    public void setCppBuyReasonList(List<String> cppBuyReasonList) {
        this.cppBuyReasonList = cppBuyReasonList;
    }

    public List<String> getCppBuyReasonList() {
        if (cppBuyReasonList == null)
            cppBuyReasonList = new ArrayList<>();

        return cppBuyReasonList;
    }

    public void setIsAdviceReceived(String isAdviceReceived) {
        this.isAdviceReceived = isAdviceReceived;

        if (TextUtils.isEmpty(isAdviceReceived) || CameroonSurvey.answerIdList[1].equals(isAdviceReceived))
            setAdviceSourceList(null);
    }

    public String getIsAdviceReceived() {
        return isAdviceReceived;
    }

    public void setAdviceSourceList(List<String> adviceSourceList) {
        this.adviceSourceList = adviceSourceList;
    }

    public List<String> getAdviceSourceList() {
        if (adviceSourceList == null)
            adviceSourceList = new ArrayList<>();

        return adviceSourceList;
    }
}
