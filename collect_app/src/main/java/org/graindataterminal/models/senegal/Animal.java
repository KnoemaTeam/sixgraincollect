package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Animal implements Cloneable {
    @SerializedName("title")
    private String title;

    @SerializedName("race_name")
    private String raceName;

    @SerializedName("production_race")
    private List<String> trainedProduction;

    @SerializedName("race_eu")
    private List<String> racesHeld;

    @SerializedName("alimentation")
    private List<String> powerMode;

    @SerializedName("complementation")
    private List<String> supplementationType;

    @SerializedName("vaccin")
    private List<String> practiceVaccination;

    @SerializedName("depara")
    private List<String> practiceDeworming;

    @SerializedName("amelioration_genetique")
    private List<String> practiceGeneticImprovement;

    @SerializedName("sanitaire")
    private List<String> sanitaryControl;

    @SerializedName("production_use")
    private List<String> mainUseProduction;

    public Animal() {

    }

    public Animal clone () {
        try {
            return (Animal) super.clone();
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

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setTrainedProduction(List<String> trainedProduction) {
        this.trainedProduction = trainedProduction;
    }

    public List<String> getTrainedProduction() {
        if (trainedProduction == null)
            trainedProduction = new ArrayList<>();

        return trainedProduction;
    }

    public void setRacesHeld(List<String> racesHeld) {
        this.racesHeld = racesHeld;
    }

    public List<String> getRacesHeld() {
        if (racesHeld == null)
            racesHeld = new ArrayList<>();

        return racesHeld;
    }

    public void setPowerMode(List<String> powerMode) {
        this.powerMode = powerMode;
    }

    public List<String> getPowerMode() {
        if (powerMode == null)
            powerMode = new ArrayList<>();

        return powerMode;
    }

    public void setSupplementationType(List<String> supplementationType) {
        this.supplementationType = supplementationType;
    }

    public List<String> getSupplementationType() {
        if (supplementationType == null)
            supplementationType = new ArrayList<>();

        return supplementationType;
    }

    public void setPracticeVaccination(List<String> practiceVaccination) {
        this.practiceVaccination = practiceVaccination;
    }

    public List<String> getPracticeVaccination() {
        if (practiceVaccination == null)
            practiceVaccination = new ArrayList<>();

        return practiceVaccination;
    }

    public void setPracticeDeworming(List<String> practiceDeworming) {
        this.practiceDeworming = practiceDeworming;
    }

    public List<String> getPracticeDeworming() {
        if (practiceDeworming == null)
            practiceDeworming = new ArrayList<>();

        return practiceDeworming;
    }

    public void setPracticeGeneticImprovement(List<String> practiceGeneticImprovement) {
        this.practiceGeneticImprovement = practiceGeneticImprovement;
    }

    public List<String> getPracticeGeneticImprovement() {
        if (practiceGeneticImprovement == null)
            practiceGeneticImprovement = new ArrayList<>();

        return practiceGeneticImprovement;
    }

    public void setSanitaryControl(List<String> sanitaryControl) {
        this.sanitaryControl = sanitaryControl;
    }

    public List<String> getSanitaryControl() {
        if (sanitaryControl == null)
            sanitaryControl = new ArrayList<>();

        return sanitaryControl;
    }

    public void setMainUseProduction(List<String> mainUseProduction) {
        this.mainUseProduction = mainUseProduction;
    }

    public List<String> getMainUseProduction() {
        if (mainUseProduction == null)
            mainUseProduction = new ArrayList<>();

        return mainUseProduction;
    }
}
