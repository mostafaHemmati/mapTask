package com.hemmati.maptask.repository.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Step {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("distance")
    @Expose
    private Distance__1 distance;
    @SerializedName("duration")
    @Expose
    private Duration__1 duration;
    @SerializedName("polyline")
    @Expose
    private String polyline;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;
    @SerializedName("start_location")
    @Expose
    private List<Double> startLocation = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Distance__1 getDistance() {
        return distance;
    }

    public void setDistance(Distance__1 distance) {
        this.distance = distance;
    }

    public Duration__1 getDuration() {
        return duration;
    }

    public void setDuration(Duration__1 duration) {
        this.duration = duration;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public List<Double> getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(List<Double> startLocation) {
        this.startLocation = startLocation;
    }

}
