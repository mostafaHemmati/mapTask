package com.hemmati.maptask.repository.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance__1 {

    @SerializedName("value")
    @Expose
    private Integer value;
    @SerializedName("text")
    @Expose
    private String text;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}