package com.springboot.httpInterface.dao;

import com.springboot.httpInterface.entity.RyDataLarge;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/8/1.
 */

@Repository
@Mapper
public interface RyDataLargeMapper {
    List<RyDataLarge> getRyDataById(Map indexId);
    List<Map>getAllPark(Map m);
    List<Map>getAllRoute(Map m);
    List<Map>getWebServiceData(Map m);
    void updateWebService(Map m);
    List<Map>getWeather(Map m);
    List<Map>getUpdateTableInfo(Map m);
    void deleteUpdateTableInfo(Map m);
    List<Map>getAllGuest(Map m);
}
