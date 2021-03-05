package com.springboot.common;

import com.alibaba.fastjson.JSONObject;
import com.mchange.v2.collection.MapEntry;
import com.springboot.httpInterface.SpringContextUtil;
import com.springboot.httpInterface.services.RyDataLargeService;
import org.apache.commons.collections.map.HashedMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/8/9.
 */
public class UpdTblProducter {
    static RyDataLargeService ryDataLargeService;
    final String topic = "ta_data_upt_msg";//固定写入

    final Map<String, String> needUpdMsgTbl = new HashedMap() {{
        put("cqyl_pre.PARK_VEHIC_DRV_IN_EVT", "t80_ta_park_consm_info");
        put("cqyl_pre.PARK_PARK_SPC_RESV_INFO", "t80_ta_park_consm_info");
        put("cqyl_ta.T80_TA_EXPO_AUDI_INFO", "t80_ta_expo_audi_info");
        put("cqyl_pre.PARK_PARKING_LOT", "t80_ta_park_lot_info");
        put("cqyl_pre.BUS_ROUTE", "t80_ta_pub_traf_info");
        put("cqyl_pre.BUS_STATION", "t80_ta_pub_traf_info");
        put("cqyl_pre.PARK_VEHIC_START_OUT_EVT", "t80_ta_park_consm_info");
        put("cqyl_pre.PARK_PAY_INFO","t80_ta_pay_info");
    }};

    public UpdTblProducter(String table,Date date) {
        if(needUpdMsgTbl.get(table)==null)
            return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map.Entry<String, String> map : needUpdMsgTbl.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            if (map.getKey().equalsIgnoreCase(table)) {
                jsonObject.put("table_name",map.getValue());
                jsonObject.put("update_time", simpleDateFormat.format(date==null?new Date():date));
                KafkaProducer kafkaProducer = new KafkaProducer(topic, jsonObject.toJSONString());
                kafkaProducer.run();
                break;
            }
        }
    }

    public UpdTblProducter(String table){
        if(needUpdMsgTbl.get(table)==null)
            return;
        Map mm = new HashedMap();
        mm.put("table_name",needUpdMsgTbl.get(table));
        if(ryDataLargeService == null)
            ryDataLargeService = (RyDataLargeService) SpringContextUtil.getBean(RyDataLargeService.class);
        List<Map> listUpd = ryDataLargeService.getUpdateTableInfo(mm);
        if(listUpd.size()<=0)
            return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Map m:listUpd){
            JSONObject jsonObject = new JSONObject();
            for(Object entry:m.entrySet()){
                Map.Entry<String,String> map = (Map.Entry<String,String>)entry;
                if(!map.getKey().equalsIgnoreCase("stat"))
                    if(!map.getKey().equalsIgnoreCase("update_time"))
                        jsonObject.put(map.getKey(),map.getValue());
                    else
                        jsonObject.put(map.getKey(),simpleDateFormat.format(map.getValue()));
            }
            KafkaProducer kafkaProducer = new KafkaProducer(topic,jsonObject.toJSONString());
            kafkaProducer.run();
            //删除数据
            ryDataLargeService.deleteUpdateTableInfo(m);
//            break;
        }
//        //删除所有表数据
//        ryDataLargeService.deleteUpdateTableInfo(mm);
    }
}
