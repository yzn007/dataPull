package com.springboot.httpInterface.entity;

import java.util.Date;

/**
 * Created by yzn00 on 2019/7/11.
 */
public class Route {
    private Date dataTm;
    private String id;
    private String code;
    private int state;
    private String name;
    private String startSiteName;
    private float startSiteLatitude;
    private float startSiteLongitude;
    private String endSiteName;
    private float endSiteLatitude;
    private float endSiteLongitude;
    private Date departTime;
    private Date returnTime;
    private float ticketPrice;

    public Date getDataTm() {
        return dataTm;
    }

    public void setDataTm(Date dataTm) {
        this.dataTm = dataTm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public float getStartSiteLatitude() {
        return startSiteLatitude;
    }

    public void setStartSiteLatitude(float startSiteLatitude) {
        this.startSiteLatitude = startSiteLatitude;
    }

    public float getStartSiteLongitude() {
        return startSiteLongitude;
    }

    public void setStartSiteLongitude(float startSiteLongitude) {
        this.startSiteLongitude = startSiteLongitude;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public float getEndSiteLatitude() {
        return endSiteLatitude;
    }

    public void setEndSiteLatitude(float endSiteLatitude) {
        this.endSiteLatitude = endSiteLatitude;
    }

    public float getEndSiteLongitude() {
        return endSiteLongitude;
    }

    public void setEndSiteLongitude(float endSiteLongitude) {
        this.endSiteLongitude = endSiteLongitude;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
