package com.springboot.common;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.scala.SaveModelData;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.IOException;
import java.util.*;

/**
 * Created by yzn00 on 2019/6/27.
 */
public class JsonObjectToAttach {

    private static Document document;
    private static  Document tplDocument;
    private static Document preDocument;
    /**
     * 取得json数组
     * @param jsonString
     * @param dataName
     * @param maxDeep
     * @return
     */
    public static String[] getJsonList(String jsonString,String dataName,boolean maxDeep) {
        List<String> listS = null;
        String dataJsonListNm = "results";
        if(!StringUtils.isEmpty(dataName))
           dataJsonListNm = dataName;
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            if(maxDeep){
                //相同结果节点，true取得最深
                while(true) {
                    try {
                        jsonObject = JSONObject.parseObject(jsonObject.get(dataJsonListNm).toString());
                    }catch (Exception ex){
                        break;
                    }
                }
                listS = JSONArray.parseArray(jsonObject.toString(), String.class);
            }else
                listS = JSONArray.parseArray(jsonObject.get(dataJsonListNm).toString(), String.class);

            String[] a = new String[listS.size()];
            int i = 0;
            for (String r : listS) {
                a[i++] = r;
            }
            return a;
        } catch (Exception e) {
            JSONObject jsonObject=null;
            Object obj = null;
            try{
                jsonObject = JSONObject.parseObject(jsonString);
                if(maxDeep){
                    //相同结果节点，true取得最深
                    while(true) {
                        try {
                            jsonObject = JSONObject.parseObject(jsonObject.get(dataJsonListNm).toString());
                        }catch (Exception ex){
                            break;
                        }
                    }
                }
                obj = jsonObject.get(dataJsonListNm);
                if (obj==null)
                    obj = jsonObject;
            }catch (Exception ex){
                listS = JSONArray.parseArray(jsonString, String.class);

                if(maxDeep){
                    //相同结果节点，true取得最深
                    Map m = new HashMap();
                    m.put(dataJsonListNm,listS);
                    jsonObject = new JSONObject(m);
                    while(true) {
                        try {
                            jsonObject = JSONObject.parseObject(jsonObject.get(dataJsonListNm).toString());
                        }catch (Exception exb){
                            break;
                        }
                    }
                    listS = JSONArray.parseArray(jsonObject.toString(), String.class);
                }
                String[] a = new String[listS.size()];
                int i = 0;
                for (String r : listS) {
                    a[i++] = r;
                }
                return a;
            }
            return new String[]{obj.toString()};
        }
    }

    /**
     * 取得dom4j对象
     * @param fileName
     * @return
     */
    public static Document parseDom4j(String fileName) {
        try {
            // 创建dom4j解析器
//            File file = new File(fileName);
            SAXReader reader = new SAXReader();

            Document document = reader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
//            System.out.println("Root element :" + document.getRootElement().getName());
            return document;
        } catch (DocumentException e) {
            System.out.print(e.toString());
        }
        return null;
    }

    /**
     * 格式化值字符串'value1','value2'……
     * @param strSplit
     * @return
     */
    public static String getJoinString(String strSplit) {
        String headStr = "";
        String tailStr = "";
        String midStr = "";
        if(strSplit.indexOf(";")>-1){
            int lastCInx = strSplit.lastIndexOf(";");
            int tailInx = strSplit.indexOf(",",lastCInx);
            tailInx = strSplit.indexOf(",",tailInx+1)>-1?strSplit.indexOf(",",tailInx+1):strSplit.length();
            int firstInx = strSplit.indexOf(";");
            int firstC = 0;
            int firstS = 0;
            int i = 0;
            int k = 0;

            String subStr = strSplit;
            while(firstC<firstInx){
                i =subStr.indexOf(",");
                subStr = subStr.substring(i+1>subStr.length()?subStr.length():i+1);
                if(StringUtils.isEmpty(subStr))
                    break;
                firstC +=i+1;
                firstC +=i+1;
                if(++k%2==0 && firstC<firstInx)//保留前两个
                    firstS = firstC;
            }
            headStr = strSplit.substring(0,firstS-1>0?firstS-1:0);
            midStr = strSplit.substring(firstS,tailInx);
            tailStr = strSplit.substring(tailInx+1>strSplit.length()?strSplit.length():tailInx+1,strSplit.length());
            return getJoinString(headStr) + (StringUtils.isEmpty(headStr)?"'":",'") +midStr + (!StringUtils.isEmpty(tailStr)?"'," :"'")+ getJoinString(tailStr);
        }else {

            String[] ars = strSplit.split(",");
            if(ars.length==1)
                return "'" + ars[0] + "'";
            else if(strSplit.length() == strSplit.lastIndexOf(",")+1){//最后一个为空如"2,3,"
                String []argT = new String [ars.length+1];
                for(int j =0;j<argT.length;j++){
                    argT[j] = j<ars.length?ars[j]:"";
                }
                ars = argT;
            }
            StringBuffer statement = new StringBuffer();
            for (int i = 0; i < ars.length; i++) {
                ars[i] = ars[i].replaceAll("，",",");
                if (i == 0)
                    statement.append("'" + ars[i] + "','");
                else if (i < ars.length - 1)
                    statement.append(ars[i] + "','");
                else
                    statement.append(ars[i] + "'");
            }
            return statement.toString();
        }
    }

    public static Map<String,String> getTableTextCode(String propertyNm,String topicPath,boolean isStatic){
        Map <String,String>m = new HashMap();
        String fileName = "Topic.xml";
        if(!StringUtils.isEmpty(topicPath))
            fileName = topicPath;

        try{
//            String path = ResourceUtils.getURL("classpath:").getPath()+ fileName;
            if(document==null)
                document = parseDom4j(fileName);
            Element root = document.getRootElement();
            for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
                Element tblEle = (Element) iterator.next();
                if (tblEle.attribute(0).getValue().equals(propertyNm)) {
                    for (Element e : tblEle.elements()) {
                        if(  e.attribute(2).getValue().toLowerCase().equals("true")) {
                            if (Boolean.parseBoolean(e.attribute(6).getValue().toString()) == isStatic) {
                                String tbs = e.attribute(1).getValue();//table多个
                                String cds = e.attribute(3).getValue();//textCode包含多个
                                if (cds.indexOf(";") > -1 && tbs.indexOf(";")>-1) {//一个topic多个textCode
                                    String []tblArray = tbs.split(";");
                                    String []cdArray = cds.split(";");
                                    for (int k=0;k<tblArray.length;k++) {
                                        if(cdArray.length>k)
                                            m.put(tblArray[k], cdArray[k]);
                                        else
                                            break;
                                    }
                                }
                            }
                        }
                     }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return m;
    }

    /***
     * 取得第0：key，1：表，4：是否删除，5：是否清空，7：url（静态isStatic=true）属性
     * @param propertyNm
     * @param topicPath
     * @param code
     * @param isStatic
     * @return
     */
    public static Map<String,String> getValidProperties(String propertyNm,String topicPath,String code,boolean isStatic){
        Map <String,String>m = new HashMap();
        String fileName = "Topic.xml";
        if(!StringUtils.isEmpty(topicPath))
            fileName = topicPath;
//        String path = Thread.currentThread().getClass().getResource("/").getPath() + fileName;


        try{
//            String path = ResourceUtils.getURL("classpath:").getPath()+ fileName;
            if(document==null)
                document = parseDom4j(fileName);
            Element root = document.getRootElement();
            for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
                Element tblEle = (Element) iterator.next();
                if (tblEle.attribute(0).getValue().equals(propertyNm)||StringUtils.isEmpty(propertyNm)) {//为空表示全部获取（生产者）
                    for (Element e : tblEle.elements()) {
                        if(  e.attribute(2).getValue().toLowerCase().equals("true"))
                            if(Boolean.parseBoolean(e.attribute(6).getValue().toString())==isStatic) {

                                    if (!StringUtils.isEmpty(code)){//生产者topic取得
                                        String cds = e.attribute(3).getValue();//textCode
                                        if(cds.indexOf(";")>-1){//一个topic多个textCode
                                            for(String cd:cds.split(";")){
                                                if (!StringUtils.isEmpty(cd) && cd.equals(code)) {//生产者topic取得
                                                    m.put(e.attribute(0).getValue(), e.attribute(1).getValue());
                                                    break;
                                                }
                                            }
                                        }else if(code.equals(cds))
                                            m.put(e.attribute(0).getValue(), e.attribute(1).getValue());
                                    }
                                    else //返回消费者topics
                                        if (!isStatic)
                                            m.put(e.attribute(0).getValue(), e.attribute(1).getValue() + "," + e.attribute(4).getValue() + "," + e.attribute(5).getValue());
                                        else
                                            m.put(e.attribute(0).getValue(), e.attribute(1).getValue() + "," + e.attribute(4).getValue() + "," + e.attribute(5).getValue() + "," + e.attribute(7).getValue());
                            }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return m;
    }

    /**
     * 取得json对应得表字段即where条件从句
     *
     * @param table
     * @param tmpFile
     * @param subTabs 子表带linkId
     * @return
     * @throws IOException
     */
    public static Map getPropertyRelation(String table, String tmpFile,List<Map<String,String>> subTabs) throws IOException {
//        String tm = cols.toLowerCase();
        String [] rets = null;
        Map cols = new HashMap();
        List<String> whereStr = new ArrayList<>();
        String spChr = "#";
        String fileName = "TableModelSet.xml";
        if (!StringUtils.isEmpty(tmpFile))
            fileName = tmpFile;

        Map<String, String> m = new HashMap();

        try {

//            String path = Thread.currentThread().getClass().getResource("/").getPath() + fileName;
//            String path = ResourceUtils.getURL("classpath:").getPath()+fileName;
            if(tplDocument==null)
                tplDocument = parseDom4j(fileName);
            Element root = tplDocument.getRootElement();
            for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
                Element tblEle = (Element) iterator.next();
                String tableName = tblEle.attribute(0).getValue();
                boolean isContain = false;
                if(tableName.indexOf(table)>-1 && (StringUtils.isEmpty(tableName.substring(tableName.indexOf(table)+table.length(),
                        tableName.indexOf(table)+table.length()+1>tableName.length()?tableName.length():tableName.indexOf(table)+table.length()+1))
                        ||tableName.substring(tableName.indexOf(table)+table.length(),
                        tableName.indexOf(table)+table.length()+1>tableName.length()?tableName.length():tableName.indexOf(table)+table.length()+1).equals(";")))
                    isContain = true;
                if (isContain || tableName.equals(table)) {
                    for (Element e : tblEle.elements()) {
                        //存在子表
                        if(e.attribute(1).getValue().indexOf("[list]")>-1){//子表列处理
//                            if(subTabs.size()>0) {
                            isContain = false;
//                            if(null!=subTabs)
                            for(Map<String,String> sub:subTabs){
                                if(null!=sub.get(e.attribute(0).getStringValue())){
                                    isContain = true;
                                    break;
                                }
                            }
                            //不包含子表
                            if(!isContain){
                                Map map = new HashMap();
                                map.put(e.attribute(0).getStringValue(),
                                            e.attribute(1).getValue().substring(e.attribute(1).getValue().indexOf("[list]")+6,
                                                e.attribute(1).getValue().length()));

                                //关联主表Id
                                map.put("{linkId}",e.attribute(2).getValue());
                                subTabs.add(map);
                            }
//                                for(Map mp :subTabs){
//                                    if(mp.get(e.attribute(0).getStringValue())!=null)
//                                        continue;
//
//                                    Map map = new HashMap();
//                                    map.put(e.attribute(0).getStringValue(),
//                                                e.attribute(1).getValue().substring(e.attribute(1).getValue().indexOf("[list]")+6,
//                                                    e.attribute(1).getValue().length()));
//
//                                    //关联主表Id
//                                    map.put("{linkId}",e.attribute(2).getValue());
//                                    subTabs.add(map);
//                                }
//                            }
//                            else {
//                                Map map = new HashMap();
//                                map.put(e.attribute(0).getStringValue(),
//                                            e.attribute(1).getValue().substring(e.attribute(1).getValue().indexOf("[list]")+6,
//                                                e.attribute(1).getValue().length()));
//                                //关联主表Id
//                                map.put("{linkId}",e.attribute(2).getValue());
//                                subTabs.add(map);
//                            }
//                            //判断是否包含linkId,并置入特殊标记
//                            if(null==m.get(e.attribute(2).getValue())){
//                                m.put(e.attribute(2).getValue(),"%$*Link Id*$%");
//                            }
                        }else//普通列处理
                            //{json对应的key，数据库列名(+#+条件或"")}
                            m.put(e.attribute(1).getValue(),
                                    e.attribute(0).getValue() + (!StringUtils.isEmpty(e.getStringValue())?
                                            spChr+e.getStringValue():""));
                    }
                }
            }

//            Map<String,Map<String,String>> mapKeys = new HashMap<>();
//            if(mapKeys==null)
//                mapKeys = new HashMap<String,Map<String,String>>();
            for (Map.Entry<String, String> e : m.entrySet()) {//处理普通列的where从句
                String value = e.getValue().toLowerCase();
                if(e.getValue().indexOf(spChr)>-1){
                    //column名+{json对应的key}
                    whereStr.add(e.getValue().split(spChr)[1]+"{"+ e.getKey() +"}");
                    //小写的column名
                    value = value.split(spChr)[0];
                }
//                if(e.getKey().indexOf(".")>0){//处理json的key中有.的情况，如：OwnRoute.Name，实际：key是.后的字段Name，前面的是子表名：OwnRoute
//                    String key = e.getKey().substring(0,e.getKey().indexOf("."));
                    //判断是否包括该列（下一个字符为逗号或空，为最后一个字段）
//                    if(tm.indexOf(key.toLowerCase())>-1 && (tm.substring(tm.indexOf(key.toLowerCase())+
//                                           key.length(),tm.indexOf(key.toLowerCase())+key.length()+1>tm.length()?
//                                            tm.length():
//                                            tm.indexOf(key.toLowerCase())+key.length()+1).equals(",")||
//                           StringUtils.isEmpty(tm.substring(tm.indexOf(key.toLowerCase())+key.length(),
//                                   tm.indexOf(key.toLowerCase())+key.length()+1>tm.length()?
//                                           tm.length():tm.indexOf(key.toLowerCase())+key.length()+1))
//                    ))
//                    if(cols.containsKey(key.toLowerCase())){
//                        if(mapKeys.get(key)==null){//判断是否存在子表名
//                            Map mp = new HashMap();
//                            mp.put(e.getKey().substring(key.length()+1,e.getKey().length()),e.getValue());
//                            mapKeys.put(key,mp);
//                        }else{
//                            Map mp = mapKeys.get(key);
//                            mp.put(e.getKey().substring(key.length()+1,e.getKey().length()),e.getValue());
//                            mapKeys.put(key,mp);
//                        }
//                    }
//                }
//                else if(!linkId.equals(e.getKey())) {//已经处理过的linkId存在跳过
////                    tm = tm.toLowerCase().replace(e.getKey().toLowerCase(),value);
//                    tm = replace(tm.toLowerCase(),e.getKey().toLowerCase(),value,"");
//                }
//                else if(tm.lastIndexOf(e.getKey())>0 && linkId.equals(e.getKey())){
//                    //跳过linkId
//                    String tmpS = tm.substring(linkId.length(),tm.length());
////                    tmpS = tmpS.toLowerCase().replace(e.getKey().toLowerCase(),value);
//                    //替换成实际column名，最前面拼上linkId
//                    tmpS =replace(tmpS.toLowerCase(),e.getKey().toLowerCase(),value,"");
//                    tm = tm.substring(0,linkId.length())+tmpS;
//                }
//                //json对应为为实际的列名

                cols.put(e.getKey(),value);


//                }
            }

//            //处理存在LinkId的情况
//            int i= 0;
//            while(true){
//                String ky = "";
//                for(Object o:cols.entrySet()){
//                    Map.Entry mo = (Map.Entry)o;
//                    //查找linkID根据特殊标记
//                    if(mo.getValue().toString().equalsIgnoreCase("%$*Link Id*$%")){
//                        ky = mo.getKey().toString();
//                        break;
//                    }
//                }
//                //找到实际的字段
//                String actulKey = "";
//                if(!StringUtils.isEmpty(ky))
//                    for(Object o:cols.entrySet()){
//                        Map.Entry mo = (Map.Entry)o;
//                        if(mo.getValue().toString().equals(ky)){
//                            actulKey = mo.getKey().toString();
//                            break;
//                        }
//                    }
//
//                if(StringUtils.isEmpty(ky))
//                    break;
//                else if(!StringUtils.isEmpty(actulKey))
//                    cols.remove(ky);
//                if(i++>10) {//防止错误设置找不到linkId的key死循环
////                        throw new Exception("\n\nsetting linkID error!\n\n");
//                    //默认跟数据库字段一致
//                    cols.put(ky,ky);
//                    break;
//                }
//            }


//            for(Map.Entry<String,Map<String,String>> mp:mapKeys.entrySet()){//处理包含.的列名
                //判断是否包含该列，包含条件为后一位字符为","或空
//                if(tm.indexOf(mp.getKey().toLowerCase())>-1 && (tm.substring(tm.indexOf(mp.getKey().toLowerCase())+ mp.getKey().length(),
//                                tm.indexOf(mp.getKey().toLowerCase())+
//                                        mp.getKey().length()+1>tm.length()?
//                                            tm.length():
//                                            tm.indexOf(mp.getKey().toLowerCase())+ mp.getKey().length()+1).equals(",")||
//                        StringUtils.isEmpty(tm.substring(tm.indexOf(mp.getKey().toLowerCase())+ mp.getKey().length(),
//                                tm.indexOf(mp.getKey().toLowerCase())+
//                                        mp.getKey().length()+1>tm.length()?
//                                            tm.length():
//                                            tm.indexOf(mp.getKey().toLowerCase())+ mp.getKey().length()+1)))){
//                    Map <String,String>val = mp.getValue();
//                    if(!cols.containsKey(mp.getKey()))
//                        continue;
//                    //删除子表标志符
//                    cols.remove(mp.getKey());
//                    String replacStr = "";
//                    for(Map.Entry<String,String> v:val.entrySet()){
////                        replacStr += v.getValue() +",";
//                       cols.put(mp.getKey()+"."+v.getKey().toLowerCase(),v.getValue().toLowerCase());
//                    }
//                    if(!StringUtils.isEmpty(replacStr)){
//                        replacStr = replacStr.substring(0,replacStr.length()-1);
////                        tm = tm.replace(mp.getKey().toLowerCase(),replacStr);
//                        tm = replace(tm,mp.getKey().toLowerCase(),replacStr,"");
//                    }
//                }
//            }

//            System.out.println(tm);

        } catch (Exception e) {
            e.printStackTrace();
        }
        int j = 0;
//        if(StringUtils.isEmpty(tm)) {
            rets = new String[whereStr.size()];
//        }
//        else {
//            rets = new String[whereStr.size() + 1];
//            j = 1;
//            //替换表字段，防止特殊字符//mysql：`;postgrep："
//            String []keys = tm.split(",");
//            for(String key:keys){
//                String delimiter = "`";//mysql的列名转义符如`column`
//                if(JsonObjectToAttach.config.get("DriverClassName").toString().toLowerCase().indexOf("postgresql")>-1)
//                    delimiter = "\"";//postgre列名可以用"
//                tm = replace(tm,key,delimiter+key+delimiter,"");
//            }
//            rets[0] = tm;
//        }

        for( ;j<rets.length;j++){
//            if(StringUtils.isEmpty(tm))
                rets [j] = whereStr.get(j);
//            else
//                rets [j] = whereStr.get(j-1);
        }
        Map properites = new HashMap();
        properites.put("rets",rets);
        properites.put("cols",cols);
        return properites;
    }


    /**
     * 重复字段处理
     * @param array
     * @param table
     * @param tmpFile
     * @return
     * @throws IOException
     */
    public static String[] processMutikeys(String []array, String table, String tmpFile ) throws IOException {

        String spChr = "#";
        String fileName = "TableModelSet.xml";
        if (!StringUtils.isEmpty(tmpFile))
            fileName = tmpFile;

        Map<String, String> m = new HashMap();

        try {

//            String path = Thread.currentThread().getClass().getResource("/").getPath() + fileName;
//            String path = ResourceUtils.getURL("classpath:").getPath()+fileName;
            if(tplDocument==null)
                tplDocument = parseDom4j(fileName);
            Element root = tplDocument.getRootElement();
            for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
                Element tblEle = (Element) iterator.next();
                String tableName = tblEle.attribute(0).getValue();
                boolean isContain = false;
                if(tableName.indexOf(table)>-1 && (StringUtils.isEmpty(tableName.substring(tableName.indexOf(table)+table.length(),
                        tableName.indexOf(table)+table.length()+1>tableName.length()?tableName.length():tableName.indexOf(table)+table.length()+1))
                        ||tableName.substring(tableName.indexOf(table)+table.length(),
                        tableName.indexOf(table)+table.length()+1>tableName.length()?tableName.length():tableName.indexOf(table)+table.length()+1).equals(";")))
                    isContain = true;
                if (isContain || tableName.equals(table)) {
                    for (Element e : tblEle.elements()) {
                        m.put(e.attribute(1).getValue(), e.attribute(0).getValue()+
                                    (!StringUtils.isEmpty(e.getStringValue())?
                                            spChr+e.getStringValue():""));
                    }
                }
            }

//

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] arrayNew = new String[array.length];
        int i = 0;
        for(String jsonStr :array){
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONObject.parseObject(jsonStr);
                for(Map.Entry keyset:m.entrySet()){
                    if(null!=jsonObject.get(keyset.getValue())){
                        jsonObject.remove(keyset.getKey());
                    }
                }
                jsonStr = jsonObject.toJSONString();
                arrayNew[i++] = jsonStr;
            }catch (Exception e){
                JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                JSONArray jsonAarrayTarget = new JSONArray();
                for(Object o:jsonArray){
                    JSONObject jsobj = (JSONObject)o;
                    for(Map.Entry keyset:m.entrySet()){
                        if(null!=jsonObject.get(keyset.getValue())){
                            jsobj.remove(keyset.getKey());
                        }
                    }
                    jsonAarrayTarget.add(jsobj);
                }
                arrayNew[i++] = jsonAarrayTarget.toJSONString();
            }
        }
        return arrayNew;
//        int j = 0;
//        if(StringUtils.isEmpty(tm)) {
//            rets = new String[whereStr.size()];
//        }
//        else {
//            rets = new String[whereStr.size() + 1];
//            j = 1;
//            //替换表字段，防止特殊字符
//            String []keys = tm.split(",");
//            for(String key:keys){
//                tm = replace(tm,key,"`"+key+"`","");
//            }
//            rets[0] = tm;
//        }
//
//        for( ;j<rets.length;j++){
//            if(StringUtils.isEmpty(tm))
//                rets [j] = whereStr.get(j);
//            else
//                rets [j] = whereStr.get(j-1);
//        }
//        return m;
    }

    /**
     * 替换boolean值
     * @param jsonObject
     */
    public static void replaceBooleanString(JSONObject jsonObject){

        for(Map.Entry keyset:jsonObject.entrySet()) {
            if (null != keyset.getValue())
                if( keyset.getValue().toString().trim().equalsIgnoreCase("false"))
                    jsonObject.put(keyset.getKey().toString(),"0");
                else if(keyset.getValue().toString().trim().equalsIgnoreCase("true"))
                    jsonObject.put(keyset.getKey().toString(),"1");
        }
    }

    /**
     * 替换块内字符，防止替换局部字符串
     * @param orignal
     * @param match
     * @param replaceS
     * @param split
     * @return
     */
    public static String replace(String orignal,String match,String replaceS,String split){
        String rtnStr = "";
        String sl = StringUtils.isEmpty(split)?",":split;
        if(orignal.indexOf(sl)>-1){
            String mat[] = orignal.split(sl);
            int len = 0;
            int i = 0;
            for(;i<mat.length;i++){
                if(mat[i].equals(match))
                    break;
                len += mat[i].length();
                if(i<mat.length-1)
                    len += sl.length();
            }
            String head = orignal.substring(0,len);
            String mid = replaceS;
            String tail = orignal.substring(i<mat.length?len+mat[i].length()+sl.length()>orignal.length()?orignal.length():
                    len+mat[i].length()+sl.length():orignal.length());
            rtnStr = !StringUtils.isEmpty(head) || !StringUtils.isEmpty(tail) ? head + (!StringUtils.isEmpty(tail)?mid+sl:mid) + tail:orignal;
            if(head.equals(orignal))
                rtnStr = orignal;
        }else {
            rtnStr = orignal.replace(match,replaceS);
        }
        return rtnStr;
    }

    /**
     * 取得json字符串的key或value
     *
     * @param jsonObject
     * @param ky
     * @param noContains
     * @param linkId
     * @param keyValueMap
     * @return
     */


    public static Map getColumsOrValues(JSONObject jsonObject, Map ky,Map noContains,String linkId,Map <String,Object>keyValueMap) {
//        String[] a = new String[1];
//
//        if(!StringUtils.isEmpty(linkId))
//            if(isCol)
//                a[0] = linkId+",";
//            else
//                a[0] = ky.get(linkId)+",";
//        if(noContains.get("linkId")!= null && StringUtils.isEmpty(linkId))//设置linkId为空
//            noContains.put("linkId","");
//        jsonObject.entrySet().iterator().forEachRemaining(s ->
//                a[0] = (!StringUtils.isEmpty(a[0]) ? a[0] : "") + (noContains.get(s.getKey().toString())!=null?"":
//                        (isCol ?  s.getKey() : ky.get(s.getKey())==null|| (!ky.get(s.getKey()).equals(linkId) && !StringUtils.isEmpty(linkId)
//                        )?(s.getValue().toString().indexOf(";")<0?s.getValue().toString().replaceAll(",","，")
//                                :s.getValue().toString()):
//                                (ky.get(s.getKey()).toString().indexOf(";")<0?ky.get(s.getKey()).toString().replaceAll(",","，")
//                                        :ky.get(s.getKey()))  ) + ","));
//
//        if(!isCol) {
//
//            for (Map.Entry<String, Map<String, String>> m : keyValueMap.entrySet()) {
//                if (jsonObject.get(m.getKey())!=null) {
//                    Map<String, String> val = m.getValue();
//                    String values = "";
//                    JSONObject josnM = JSONObject.parseObject(jsonObject.get(m.getKey()).toString());
//                    for (Map.Entry<String, String> v : val.entrySet()) {
//                        String value = "";
//                        try{
//                            value = josnM.get(v.getKey()).toString();
//                        }catch (Exception ee){
//                            System.out.println("error【"+v.getKey()+"】:" +josnM);
//                            return null;
//                        }
//                        values += value + ",";
//                    }
//                    String subStr = jsonObject.get(m.getKey()).toString().replaceAll(",","，");
//                    String head = a[0].substring(0,a[0].indexOf(subStr)+subStr.length());
//                    String tail = a[0].substring(a[0].indexOf(subStr)+subStr.length(),a[0].length());
////                    head = head.replace(subStr,values.substring(0,values.length()-1));
//                    head = replace(head,subStr,values.substring(0,values.length()-1),"#");
//                    a[0] = head + tail;
//                }
//            }
//        }
//        return a[0].substring(0, a[0].length() - 1).replaceAll("'","\\\\'\r");
        Map colums = new HashMap();
        for(int j=0;j<linkId.split(",").length;j++){
            String linkIdSl = linkId.split(",")[j];
            if (!StringUtils.isEmpty(linkIdSl))
                if (null != ky.get(linkIdSl))
                    if (keyValueMap.containsKey(linkIdSl))
                        colums.put(keyValueMap.get(linkIdSl), ky.get(linkIdSl));
                    else
                        colums.put(linkIdSl, ky.get(linkIdSl));
                else
                    colums.put(linkIdSl, "");
        }

        for(Object keys :jsonObject.entrySet()){
            Map.Entry mapKey  = (Map.Entry)keys;
            if(!noContains.containsKey(mapKey.getKey()))
                colums.put(mapKey.getKey(),mapKey.getValue());
        }



        for(Map.Entry<String,Object> m:keyValueMap.entrySet()){
            if (jsonObject.get(m.getKey())!=null && !StringUtils.isEmpty(m.getKey())) {
                //删除原来的key
                colums.remove(m.getKey());
                colums.put(m.getValue(),jsonObject.get(m.getKey()));
                //有包括.的key，实际json是嵌套一个json对象，需要拆开最后合并
            }else if(m.getKey().indexOf(".")>-1 && null != jsonObject.get(m.getKey().substring(0,m.getKey().indexOf(".")))){
                JSONObject jsonChild = JSONObject.parseObject(jsonObject.get(m.getKey().substring(0,m.getKey().indexOf("."))).toString());
                String key = m.getKey().substring(0,m.getKey().indexOf("."));
                String secondKey = m.getKey().substring(m.getKey().indexOf(".")+1);
                for(Map.Entry child :jsonChild.entrySet()){
                    if(null!=colums.get(key+"."+secondKey) && child.getKey().toString().equals(secondKey)) {
                        //把实际列名取出
                        String actualKey = colums.get(key+"."+secondKey).toString();
                        colums.remove(key+"."+secondKey);
                        colums.put(actualKey, child.getValue());
                    }
                }
            }else if(jsonObject.get(m.getValue())!=null){//找不到尝试根据value
                colums.put(m.getValue(),jsonObject.get(m.getValue()));
                System.out.format("\n{}key no found,try value find!!!\n",jsonObject.toJSONString());
            }
        }
        return colums;
    }


    /**
     * 取得嵌套json得表名
     * @param jsonObject
     * @return
     */
    public static String getDataListTableNm(JSONObject jsonObject) {
        String[] a = new String[1];

        jsonObject.entrySet().iterator().forEachRemaining(s ->
                a[0] = (a[0] != null ? a[0] : "") + (s.getValue().toString().indexOf("[list]")>-1?
                        s.getValue().toString().substring(s.getValue().toString().indexOf("[list]")+6,s.getValue().toString().length()-1)+",":""));

        return a[0].substring(0, a[0].length() - 1);
    }

    /**
     * 返回Map的key或values
     * @param columnValues
     * @param isCols
     * @return
     */
    public static String getValuesByKeys(Map columnValues,boolean isCols) {

        //默认mysql转义符
        String delimiter = "`";
        if(JsonObjectToAttach.config.get("DriverClassName").toString().toLowerCase().indexOf("postgresql")>-1)
            delimiter = "\"";//postgre列名可以用"
        String retVals  = "";
        String splitChr = "'";

        for(Object object:columnValues.entrySet()){
            Map.Entry map = (Map.Entry) object;
            if(isCols)
                retVals += delimiter + map.getKey()+ delimiter + ",";
            else
                retVals += splitChr + map.getValue() + splitChr + ",";
        }
        return retVals.substring(0,retVals.length()-1>0? retVals.length()-1:0);
    }

    /**
     * 根据keys取得值连接串
     * @param jsonObject
     * @param keys
     * @param delimet
     * @return
     */
    public static String getValuesByKeys(JSONObject jsonObject,String keys,String delimet) {
        String retVals  = "";
        String splitChr = "\\+";
        if(!StringUtils.isEmpty(delimet)){
            splitChr = delimet;
        }
        String[] ks = keys.split(splitChr);
        for(int k=0;k<ks.length;k++){
            retVals += jsonObject.get( ks[k].trim());
        }
        return retVals;
    }



    static final Map staticTableRelation = new HashMap(){
        {
            put("GTGCDM.PUB_UNIFIED_IDENTITY_USER","TARGET_ACCOUNT");
            put("GTGCDM.PUB_UNIFIED_IDENTITY_ORG","TARGET_ORGANIZATION");
        }

    };


    /**
     * 生成插入或删除语句
     *
     * @param jsons
     * @param table
     * @param where
     * @param linkId
     * @param isModify
     * @param isTruncate
     * @return
     */
    public static String[] getBatchStatement(String[] jsons, String table,String where, String linkId, boolean isModify,Map keyWhere,boolean isTruncate) {
        List<String> att = new ArrayList<>();
//        Map keyWhere = new HashMap();
        String[] ret = null;
        if (jsons == null || jsons.length <= 0)
            return null;
        try {
            //映射数据库字段和where条件
            List<Map<String,String>> subTabs = new ArrayList<>();
            Map columnSet = null;
            //取得子表
            Map properties = getPropertyRelation(table, null,subTabs);
            String tmpLink = "";
            Map <String,String>noContainCols = new HashMap();
            for(Map<String,String> t:subTabs) {
                for (Map.Entry<String, String> e : t.entrySet()) {
                    if(!e.getKey().equals("{linkId}"))
                        noContainCols.put(e.getKey(),e.getValue());
                    else
                        tmpLink = e.getValue();
                }
            }
//            //取得json key
//            String column = getColumsOrValues(JSONObject.parseObject(jsons[0]), true,keyWhere,noContainCols,linkId,null);
//            Map keyMap = new HashMap<String,Map<String,String>>();
//            rets = getPropertyRelation(column, table, null,subTabs,linkId,keyMap);
            if(null != properties.get("cols"))
                columnSet = (Map)properties.get("cols");
            String []rets = {};
            if(null!= properties.get("rets"))
                rets = (String [])properties.get("rets");
            for (String json : jsons) {
                Map column = getColumsOrValues(JSONObject.parseObject(json), keyWhere, noContainCols, linkId, columnSet);

//                Map keyMap = new HashMap<String,Map<String,String>>();
//                properties = getPropertyRelation(table, null,subTabs,linkId);
//                Map keyColumn = new HashMap();
//                for(int k=0;k<rets.length;k++) {//实际格式：字段1=值1{json的key}
//                    String[] vals = rets[k].split("=");
//
//                    String valByKeys = "";
////                    if(keyWhere.get(vals[1].substring(0,vals[1].indexOf("{")).trim())==null){
////                        valByKeys = getValuesByKeys(JSONObject.parseObject(json),vals[1].substring(0,vals[1].indexOf("{")),"");
//                    //keyWhere.put(vals[1].substring(vals[1].indexOf("{")+1,vals[1].indexOf("}")),valByKeys);
////                    }else{
////                        valByKeys = keyWhere.get(vals[1].substring(0,vals[1].indexOf("{")).trim()).toString();
////                    }
//                    valByKeys = getValuesByKeys(JSONObject.parseObject(json), vals[1].substring(0, vals[1].indexOf("{")), "");
//                    for(int j=0;j<linkId.split(",").length;j++){//linkId多个用,分割开来
//                        String linkIdSl = linkId.split(",")[j];
//
//                        if((StringUtils.isEmpty(valByKeys )|| "null".equals(valByKeys)) && vals[0].toString().trim().equals(columnSet.get(linkIdSl))) {
//
////                        if (!vals[0].toString().trim().equals(linkId) && columnSet.containsKey(linkId)) {//子表的linkId与父不一致
////                            //添加实际的linkId对应字段
////                            keyWhere.put(vals[1].substring(vals[1].indexOf("{") + 1, vals[1].indexOf("}")), keyWhere.get(linkId));
////                        }
////                        else
//                                keyWhere.put(vals[1].substring(vals[1].indexOf("{") + 1, vals[1].indexOf("}")), column.get(column.get(columnSet.get(linkIdSl))));
//                        }else
//                            keyWhere.put(vals[1].substring(vals[1].indexOf("{") + 1, vals[1].indexOf("}")), valByKeys);
//                    }
//
////                    //实际列保存
////                    keyColumn.put(vals[1].substring(vals[1].indexOf("{") + 1, vals[1].indexOf("}")),vals[0]);
//                }
                if(rets.length>0) {
                    if (isTruncate) {
                        String truncateStr = "truncate " + table;
                        //只清空一次
                        if (!att.contains(truncateStr))
                            att.add(truncateStr);

                    } else if (isModify) {
                        String delSt = "delete from " + table + " where 1=1 ";
                        if (!StringUtils.isEmpty(where))
                            delSt += " and " + where;

//                        for(Object e :keyWhere.entrySet()){
//                            Map.Entry<String,Object> m = (Map.Entry<String,Object>)e;
////                            if(null!=keyColumn.get(m.getKey()))
////                                delSt += " and " +keyColumn.get(m.getKey()) + " = '" + m.getValue() + "'";
//                            if(null!=columnSet.get(m.getKey()))
//                                delSt += " and " + columnSet.get(m.getKey()) + " = '" + m.getValue() + "'";
//                        }

                        for(int k=0;k<rets.length;k++) {//实际格式：字段1=值1{json的key}
                            String colNm = rets[k].split("=")[0].trim();

                            delSt += " and " + colNm + " = '" + column.get(colNm) + "'";

                        }

//                        delSt += " and " + vals[0] + " = '" + valByKeys + "'";
                        if (!att.contains(delSt))
                            att.add(delSt);
                    }
                }
                else if(rets.length == 0){
                    if (isTruncate) {
                        String truncateStr = "truncate " + table;
                        //只清空一次
                        if (!att.contains(truncateStr))
                            att.add(truncateStr);

                    } else if (isModify) {
                        String delSt = "delete from " + table + " where 1=1 ";
                        if (!StringUtils.isEmpty(where))
                            delSt += " and " + where;

                        if (!att.contains(delSt))
                            att.add(delSt);
                    }
                }
                //取得json value
//                String values = getColumsOrValues(JSONObject.parseObject(json), false,keyWhere,noContainCols,linkId);
                String values= getValuesByKeys(column,false);//value
                String keys = getValuesByKeys(column,true);//keys
                if(StringUtils.isEmpty(values))
                    continue;
//                String insertSt = "insert into " + table + " (" + rets[0] + ") values(" + getJoinString(values) + ")";
//                String insertSt = "insert into " + table + " (" + keys + ") values(" + getJoinString(values) + ")";
                String insertSt = "insert into " + table + " (" + keys + ") values(" + values + ")";
                if(!att.contains(insertSt))
                    att.add(insertSt);
                //递归调用

                for (Map.Entry<String,String> e : noContainCols.entrySet()) {
                    //判断linkId是否包含keyWhere里
                    for(int k=0;k<tmpLink.split(",").length;k++) {
                        String strLink=tmpLink.split(",")[k];
                        if (null == keyWhere.get(strLink)) {
                            if (rets.length > 0){
                                for(int j=0;j<rets.length;j++){
                                    if( rets[j].indexOf(strLink) <0)
                                        continue;
                                    //取出实际json字串linkId的值
                                    String linkRep = rets[j].substring(rets[j].indexOf("{") + 1, rets[j].indexOf("}"));
                                    if (!StringUtils.isEmpty(linkRep)) {
                                        keyWhere.put(strLink, JSONObject.parseObject(json).get(linkRep));
                                        //删除父节点的key，以访字节主键跟父节点一样
                                        if (null != keyWhere.get(linkRep))
                                            keyWhere.remove(linkRep);
                                    }
                                }
                            }
                        }
                    }
                    //递归调用前，替换实际linkId
//                    if(!StringUtils.isEmpty(tmpLink)&&keyWhere.size()>0){
//                        boolean isRep = false;
//                        String key = "";
//                        Object val = "";
//                        for(Object object:keyWhere.entrySet()){
//                            Map.Entry map = (Map.Entry)object;
//                            if(map.getValue().toString().equals(tmpLink)){
//                                key = map.getKey().toString();
//                                val = map.getValue();
//                                isRep = true;
//                                break;
//                            }
//                        }
//                        if(isRep) {
//                            keyWhere.remove(key);
//                            keyWhere.put(tmpLink, val);
//                        }
//                    }
                    String[] bb = getBatchStatement(getJsonList(json, e.getKey(),false), e.getValue(), null,tmpLink, isModify, keyWhere,isTruncate);
                    if (bb == null) {//子表无数据都返回空
                        return null;
                    }
                    else
                        for (int k = 0; k < bb.length; k++) {
                            if(!att.contains(bb[k]))
                                att.add(bb[k]);
                        }

                }

                if(StringUtils.isEmpty(linkId))//子表不用清空
                    keyWhere.clear();

            }
            //清空条件，防止条件不一样，一直 用同一个条件的情况出现
            keyWhere.clear();
            if (att.size() > 0) {
                ret = new String[att.size()];
                int i = 0;
                for (String t : att) {
                    ret[i++] = t.replaceAll("''","null").replaceAll("\r","");
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return ret;
    }

    //project.properties
   public static final Map config ;
    static {
        Map map = new HashMap();
        try {
            map.putAll(ReadPropertiesUtils.readConfig("project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = Collections.unmodifiableMap(map);

    }

    /**
     * 返回嵌套children的rows
     * @param jsonObj json对象
     * @param childrenNodeName 嵌套节点名{children:[{}]}
     * @param id key名:org_id
     * @param parentVal 父code值
     */
    public static JSONArray getWhileLoopChildrens(JSONObject jsonObj,String childrenNodeName,String id,Object parentVal){
        JSONArray jsonArrayRtn = null;
        if(jsonObj.get(childrenNodeName)!=null)
            while(true){
                //是否存在子节点
                if(!jsonObj.containsKey(childrenNodeName))
                    break;

                JSONArray jsonArray = JSONArray.parseArray(jsonObj.get(childrenNodeName).toString());
                if(jsonArray!=null) {
                    for (Object obj1 : jsonArray) {
                        if(null!=jsonArrayRtn)
                            //添加同级子节点
                            jsonArrayRtn.addAll(getWhileLoopChildrens(JSONObject.parseObject(obj1.toString()), childrenNodeName, id, jsonObj.get(id)));
                        else
                            //返回头节点
                            jsonArrayRtn = getWhileLoopChildrens(JSONObject.parseObject(obj1.toString()), childrenNodeName, id, jsonObj.get(id));
                    }
                    //删除已处理节点
                    jsonObj.remove(childrenNodeName);
                }

            }
        //字节的添加父code
        JSONObject  jsonObjRetrun = new JSONObject();

        for(Map.Entry entry :jsonObj.entrySet()){
            if(entry.getKey().equals(childrenNodeName))
                continue;
            jsonObjRetrun.put(entry.getKey().toString(),entry.getValue());
            //父code字段前缀
            jsonObjRetrun.put("parent_"+id,parentVal);
        }
        if(jsonArrayRtn==null)//头节点
            jsonArrayRtn = new JSONArray();
        if(jsonArrayRtn!=null)
            jsonArrayRtn.add(jsonObjRetrun);
        return jsonArrayRtn;
    }


    public static void  main(String args[]){
        String josnStrChildrens = "{\n" +
                "  \"data\": {\n" +
                "    \"org_name\": \"全部\",\n" +
                "    \"org_id\": 0,\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"org_name\": \"厦门\",\n" +
                "        \"org_id\": 8,\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"org_name\": \"泉州区\",\n" +
                "            \"org_id\": 81,\n" +
                "            \"children\": [\n" +
                "              {\n" +
                "                \"org_name\": \"泉州城区\",\n" +
                "                \"org_id\": 811,\n" +
                "                \"children\": [\n" +
                "                  {\n" +
                "                    \"org_name\": \"城关街道\",\n" +
                "                    \"org_id\": 8111,\n" +
                "                    \"children\": [\n" +
                "                      {\n" +
                "                        \"org_name\": \"公司81111\",\n" +
                "                        \"org_id\": 81111,\n" +
                "                        \"children\": [\n" +
                "                          {\n" +
                "                            \"org_name\": \"分公司811111\",\n" +
                "                            \"org_id\": 811111\n" +
                "                          }\n" +
                "                        ]\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"org_name\": \"远郊\",\n" +
                "                    \"org_id\": 8112,\n" +
                "                    \"children\": [\n" +
                "                      {\n" +
                "                        \"org_name\": \"公司81121\",\n" +
                "                        \"org_id\": 81121,\n" +
                "                        \"children\": [\n" +
                "                          {\n" +
                "                            \"org_name\": \"分公司811211\",\n" +
                "                            \"org_id\": 811211\n" +
                "                          }\n" +
                "                        ]\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"org_name\": \"深圳\",\n" +
                "        \"org_id\": 1,\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"org_name\": \"南山区\",\n" +
                "            \"org_id\": 2,\n" +
                "            \"children\": [\n" +
                "              {\n" +
                "                \"org_name\": \"公司21\",\n" +
                "                \"org_id\": 21\n" +
                "              },\n" +
                "              {\n" +
                "                \"org_name\": \"公司22\",\n" +
                "                \"org_id\": 22\n" +
                "              },\n" +
                "              {\n" +
                "                \"org_name\": \"公司23\",\n" +
                "                \"org_id\": 23\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"org_name\": \"北山区\",\n" +
                "            \"org_id\": 3,\n" +
                "            \"children\": [\n" +
                "              {\n" +
                "                \"org_name\": \"公司31\",\n" +
                "                \"org_id\": 31\n" +
                "              },\n" +
                "              {\n" +
                "                \"org_name\": \"公司32\",\n" +
                "                \"org_id\": 32,\n" +
                "                \"children\": [\n" +
                "                  {\n" +
                "                    \"org_name\": \"公司321\",\n" +
                "                    \"org_id\": 321\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"success\": 1\n" +
                "}";
        Object obj=null;
        JSONArray jsonObjectRtn = null;
        JSONArray jsonObjectRtn1 = getWhileLoopChildrens(JSONObject.parseObject(JSONObject.parseObject(josnStrChildrens).get("data").toString()),
                "children","org_id",obj);


        String stat = "";
        stat = replace("vehnum_routeid,times_routeid,time,vehnum","time1","times_id",",");
        String  jsonValue ="{\n" +
                "  \"code\": \"1\",\n" +
                "  \"message\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"interrupt\": false,\n" +
                "    \"timestamp\": 1608537202840,\n" +
                "    \"taskId\": \"20201221154223919-14E7-734636690\",\n" +
                "    \"objectType\": \"TARGET_ACCOUNT\",\n" +
                "    \"objectCode\": \"testdemo_TargetAccount\",\n" +
                "    \"effectOn\": \"CREATED\",\n" +
                "    \"data\": {\n" +
                "      \"_user\": \"zhangsan\",\n" +
                "      \"_organization\": null,\n" +
                "      \"username\": \"zhangsan\",\n" +
                "      \"password\": null,\n" +
                "      \"fullname\": \"张三测试\",\n" +
                "      \"isDisabled\": false,\n" +
                "      \"isLocked\": false,\n" +
                "      \"createAt\": \"2020-12-21 15:42:23.000\",\n" +
                "      \"updateAt\": \"2020-12-21 15:42:23.000\",\n" +
                "      \"isSystem\": false,\n" +
                "      \"isPublic\": false,\n" +
                "      \"isMaster\": true,\n" +
                "      \"email\": \"zhangsan@crecg.com\",\n" +
                "      \"employeeNo\": null,\n" +
                "      \"mobile\": \"13247703738\",\n" +
                "      \"sex\": \"1\"\n" +
                "    },\n" +
                "    \"id\": \"20201221154223828-3236-DD9FB740B\"\n" +
                "  }\n" +
                "}";
        String tablePre = "cqyl_ta.T80_TA_EXPO_AUDI_INFO";
        String [] array = getJsonList(jsonValue,"",true);

//        System.out.println(config);
//        new KafkaSaveData("bingfu","web_data_profil").start();

        //取得有效主题
        Map<String,String> topicM = JsonObjectToAttach.getValidProperties("topics",null,null,false);

        for(Map.Entry<String, String> m : topicM.entrySet()){
            String []tabAndMark = null;
            if(m.getValue().indexOf(",")>=0){
                tabAndMark =m.getValue().split(",");
            }

            String table = tabAndMark[0];
            if(table.indexOf(tablePre)<0)
                continue;
            String isDelInsert = tabAndMark[1];
            String isTrancate = tabAndMark[2];
            List<String[]> reds = new ArrayList<>();
            List<String[]> sqlListDyc = new ArrayList<>();

            //表名固定了，根据实际情况修改
            for(int k=0;k<table.split(";").length;k++){
                //删除当前表数据，保留历史表数据
                String[] sql = JsonObjectToAttach.getBatchStatement(array, table.split(";")[k], "","",
                        !(isDelInsert.indexOf(";")>0?isDelInsert.split(";")[k]:isDelInsert).equalsIgnoreCase("false"),new HashMap(),
                        !(isTrancate.indexOf(";")>0?isTrancate.split(";")[k]:isTrancate).equalsIgnoreCase("false"));
                if(!reds.contains(sql))
                    reds.add(sql);



            }

            try {
                Seq<String[]> tmpSeq = JavaConverters.asScalaIteratorConverter(reds.iterator()).asScala().toSeq();
                if (tmpSeq.size() > 0) {
                    SaveModelData.main(tmpSeq.toList());
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
    }

}
