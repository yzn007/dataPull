package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.dao.RyDataLargeMapper;
import com.springboot.httpInterface.entity.RyDataLarge;
import com.springboot.httpInterface.services.RyDataLargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/8/1.
 */
@Service("ryDataLargeService")
public class RyDataLargeServiceImpl implements RyDataLargeService {


    @Autowired
    RyDataLargeMapper ryDataLargeMapper;
    @Override
    public List<RyDataLarge> getRyDataById(Map Id) {
        return ryDataLargeMapper.getRyDataById(Id);
    }

    @Override
    public List<Map> getAllRoute(Map map) {
        return ryDataLargeMapper.getAllRoute(map);
    }

    @Override
    public List<Map> getAllPark(Map map) {
        return ryDataLargeMapper.getAllPark(map);
    }

    @Override
    public List<Map> getWebServiceData(Map m) {
        return ryDataLargeMapper.getWebServiceData(m);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void updateWebService(Map m) {
        ryDataLargeMapper.updateWebService(m);
    }

    @Override
    public List<Map> getWeather(Map m) {
        return ryDataLargeMapper.getWeather(m);
    }

    @Override
    public List<Map> getUpdateTableInfo(Map m) {return ryDataLargeMapper.getUpdateTableInfo(m);}

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void deleteUpdateTableInfo(Map m) {
        ryDataLargeMapper.deleteUpdateTableInfo(m);
    }

    @Override
    public List<Map> getAllGuest(Map map) {
        return ryDataLargeMapper.getAllGuest(map);
    }
}
