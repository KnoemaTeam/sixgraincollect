package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RacesArrestedGroup implements Cloneable {
    public final static String[] raceHeldIdList = {"locale", "exotique", "metisse"};

    @SerializedName("race_in_exploitation")
    private List<String> raceType;

    @SerializedName("race_locale")
    private List<String> raceLocal;

    @SerializedName("race_exotique")
    private List<String> raceExotic;

    @SerializedName("race_metis")
    private List<String> raceMixed;

    public RacesArrestedGroup clone () {
        try {
            return (RacesArrestedGroup) super.clone();
        }
        catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setRaceType(List<String> raceType) {
        this.raceType = raceType;

        if (raceType == null || raceType.isEmpty()) {
            setRaceLocal(null);
            setRaceExotic(null);
            setRaceMixed(null);
        }
        else {
            if (!raceType.contains(raceHeldIdList[0]))
                setRaceLocal(null);
            else if (!raceType.contains(raceHeldIdList[1]))
                setRaceExotic(null);
            else if (!raceType.contains(raceHeldIdList[2]))
                setRaceMixed(null);
        }
    }

    public List<String> getRaceType() {
        if (raceType == null)
            raceType = new ArrayList<>();

        return raceType;
    }

    public void setRaceLocal(List<String> raceLocal) {
        this.raceLocal = raceLocal;
    }

    public List<String> getRaceLocal() {
        if (raceLocal == null)
            raceLocal = new ArrayList<>();

        return raceLocal;
    }

    public void setRaceExotic(List<String> raceExotic) {
        this.raceExotic = raceExotic;
    }

    public List<String> getRaceExotic() {
        if (raceExotic == null)
            raceExotic = new ArrayList<>();

        return raceExotic;
    }

    public void setRaceMixed(List<String> raceMixed) {
        this.raceMixed = raceMixed;
    }

    public List<String> getRaceMixed() {
        if (raceMixed == null)
            raceMixed = new ArrayList<>();

        return raceMixed;
    }
}
