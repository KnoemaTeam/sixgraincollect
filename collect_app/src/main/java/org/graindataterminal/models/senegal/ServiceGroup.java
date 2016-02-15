package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ServiceGroup implements Cloneable {
    public final static String[] serviceNameIdList = {"travail_sol", "semis", "recolte", "autre_service"};

    @SerializedName("service")
    private List<String> serviceList;

    @SerializedName("autre_service")
    private String otherService;

    public ServiceGroup clone () {
        try {
            return (ServiceGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setServiceList(List<String> serviceList) {
        this.serviceList = serviceList;

        if (serviceList == null || !serviceList.contains(serviceNameIdList[serviceNameIdList.length - 1]))
            setOtherService(null);
    }

    public List<String> getServiceList() {
        if (serviceList == null)
            serviceList = new ArrayList<>();

        return serviceList;
    }

    public void setOtherService(String otherService) {
        this.otherService = otherService;
    }

    public String getOtherService() {
        return otherService;
    }
}
