package com.springboot.httpInterface.services;


import com.springboot.httpInterface.SpringContextUtil;
import com.springboot.httpInterface.StaticContext;
import com.springboot.httpInterface.entity.JobAndTrigger;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class CxfWebService {

    private String toString(Object o){
        if(o!=null)
            return o.toString().trim();
        else
            return "";
    }

    static RyDataLargeService ryDataLargeService;
    //    public static void main(String []args){
    //        cl2();
    //    }


        @RequestMapping("cl2")
        public  String cl2() {
            // 创建动态客户端
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient("http://219.153.5.18:9081/net-ws/ws/NetWsServiceWrap?wsdl");
            // 需要密码的情况需要加上用户名和密码
            // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,
            // PASS_WORD));
            Object[] objects = new Object[0];
            try {
                // 系统类别,接口Id,接口授权码,终端用户代号,终端用户姓名,终端标识,终端电子坐标,接口XML对象解密、解密KEY,数据操作对象;
                String terminalCode = "1234";
                String terminalNm = "test";
                String terminalId = "222.178.231.71";
                String terminalXY = "192,234";
                String enKey = "DfeDFEFDxXUF&*%*##*(#$JJFK";
                String license = "21849186864529442191419884887906";

//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                Map mm = new HashedMap();
                if(ryDataLargeService == null)
//                    ryDataLargeService =  StaticContext.getContext().getBean(RyDataLargeService.class);
                    ryDataLargeService = (RyDataLargeService)SpringContextUtil.getBean(RyDataLargeService.class);
                List<Map> listWeb = ryDataLargeService.getWebServiceData(mm);
                if(listWeb.size()<=0)
                    return null;
                for(Map m :listWeb) {
                    Document document = DocumentHelper.createDocument();
                    Element root = document.addElement("root");
                    Element wrObject = root.addElement("WriteCondition");
                    Element hpzl = wrObject.addElement("hpzl");
                    //                hpzl.setText("02");
                    hpzl.setText(toString(m.get("tpHpzl")));
                    Element hphm = wrObject.addElement("hphm");
                    hphm.setText(toString(m.get("tpHphm")));
                    Element txrqq = wrObject.addElement("txrqq");
                    txrqq.setText(toString(m.get("tpTxrqq")));
                    Element txrqz = wrObject.addElement("txrqz");
                    txrqz.setText(toString(m.get("tpTxrqz")));
                    Element txfw = wrObject.addElement("txfw");
                    txfw.setText(toString(m.get("tpTxfw")));
                    Element txyy = wrObject.addElement("txyy");
                    txyy.setText(toString(m.get("tpTxyy")));
                    Element bz = wrObject.addElement("bz");
                    bz.setText("");
                    Element czlx = wrObject.addElement("czlx");
                    czlx.setText(toString(m.get("tpCzlx")));
//                    czlx.setText("1");

                    Element jksqm = wrObject.addElement("jksqm");
//                    if (null != m.get("tpJksqm") && !StringUtils.isEmpty(toString(m.get("tpJksqm"))))
//                        jksqm.setText(m.get("tpJksqm").toString());
//                    else
                        jksqm.setText(license);
                    Element bh = wrObject.addElement("bh");
                    bh.setText(toString(m.get("tpValue")));

                    StringWriter sw = new StringWriter();
                    OutputFormat opf = OutputFormat.createPrettyPrint();
                    opf.setEncoding("UTF-8");
                    XMLWriter xmlWriter = new XMLWriter(sw,opf);
                    try{
                        xmlWriter.write(document);
                    }catch (Exception e){

                    }
                    String xmlString = sw.toString();
                    int k = 0;
                    while(true) {
                        String code = "";
                        String msg = "";
                        String value = "";
                        objects = client.invoke("writeObject", "39", "39W01", license, terminalCode, terminalNm, terminalId, terminalXY, enKey, xmlString);
                        //               System.out.println("返回数据:" + objects[0]);
                        Document retDoc = DocumentHelper.parseText(objects[0].toString());
                        root = retDoc.getRootElement();
                        for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
                            Element ele = (Element) iterator.next();
                            if (ele.getName().equalsIgnoreCase("head")) {
                                List<Element> els = ele.elements();

                                for (Element s : els) {
                                    if (s.getName().equals("code")) {
                                        code = s.getText();
                                    }
                                    else if(s.getName().equalsIgnoreCase("message") ||
                                            s.getName().equalsIgnoreCase("msg") )
                                        msg = s.getText();
                                    else
                                        value = s.getText();
                                }
                                System.out.println("调用webservice返回code:"+code+"["+ decode(msg,"UTF-8")+"]");
                            }
                        }
//                            System.out.println("调用webservice返回xml:"+"["+decode(objects[0].toString()+"]","UTF-8"));
                        if(code.equalsIgnoreCase("1")){
                            m.put("tpValue",value);
                            m.put("tpStat","1"); //成功
                            ryDataLargeService.updateWebService(m);
                        }else{
                            m.put("tpMsg",decode(objects[0].toString(),"UTF-8"));
                            m.put("tpStat","2"); //失败
                            ryDataLargeService.updateWebService(m);
                        }
                        break;
//                        Thread.sleep(5000);
//                        if(k++>5)
//                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "cl2";

        }

}
