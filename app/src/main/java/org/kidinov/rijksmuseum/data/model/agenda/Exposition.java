package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Exposition extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("code")
    private String code;
    @SerializedName("controlType")
    private Integer controlType;
    @SerializedName("maxVisitorsPerGroup")
    private Integer maxVisitorsPerGroup;
    @SerializedName("maxVisitorsPerPeriodWeb")
    private Integer maxVisitorsPerPeriodWeb;
    @SerializedName("price")
    private Price price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getControlType() {
        return controlType;
    }

    public void setControlType(Integer controlType) {
        this.controlType = controlType;
    }

    public Integer getMaxVisitorsPerGroup() {
        return maxVisitorsPerGroup;
    }

    public void setMaxVisitorsPerGroup(Integer maxVisitorsPerGroup) {
        this.maxVisitorsPerGroup = maxVisitorsPerGroup;
    }

    public Integer getMaxVisitorsPerPeriodWeb() {
        return maxVisitorsPerPeriodWeb;
    }

    public void setMaxVisitorsPerPeriodWeb(Integer maxVisitorsPerPeriodWeb) {
        this.maxVisitorsPerPeriodWeb = maxVisitorsPerPeriodWeb;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

}
