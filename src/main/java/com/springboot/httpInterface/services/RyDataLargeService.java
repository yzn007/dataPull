package com.springboot.httpInterface.services;

import com.springboot.httpInterface.entity.RyDataLarge;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/8/1.
 */
public interface RyDataLargeService {
    List<RyDataLarge> getRyDataById(Map Id);
    List<Map>getAllRoute(Map map);
    List<Map>getAllGuest(Map map);
    List<Map>getAllPark(Map map);
    List<Map>getWebServiceData(Map m);
    void updateWebService(Map m);
    List<Map>getWeather(Map m);
    List<Map>getUpdateTableInfo(Map m);
    void deleteUpdateTableInfo(Map m);
}
