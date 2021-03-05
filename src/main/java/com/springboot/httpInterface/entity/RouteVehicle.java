package com.springboot.httpInterface.entity;

import java.util.Date;

/**
 * Created by yzn00 on 2019/7/27.
 */
public class RouteVehicle {
    String id;
    String platNum;
    String vehNum;
    String lat;
    String lng;
    String direct;
    String site;
    Date dateTm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatNum() {
        return platNum;
    }

    public void setPlatNum(String platNum) {
        this.platNum = platNum;
    }

    public String getVehNum() {
        return vehNum;
    }

    public void setVehNum(String vehNum) {
        this.vehNum = vehNum;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getDateTm() {
        return dateTm;
    }

    public void setDateTm(Date dateTm) {
        this.dateTm = dateTm;
    }
}
