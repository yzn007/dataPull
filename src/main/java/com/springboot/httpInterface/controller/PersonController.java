package com.springboot.httpInterface.controller;

import com.alibaba.fastjson.JSONObject;
import com.springboot.httpInterface.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yzn00 on 2019/6/19.
 */
@Controller
@RequestMapping(value = "person/")
public class PersonController {
    @Autowired
    private PersonService personService;

    @RequestMapping(value = "all", method = RequestMethod.GET)
    @ResponseBody
    public Map onAll() {
        List list = personService.selectAllPerson();
        Map m = new HashMap();
        m.put("data", JSONObject.toJSONString(list));
        try {
            Thread.sleep(500);
        }catch (Exception e){
            System.out.print("system interrupt error!");
        }
        return m;
    }

    @RequestMapping(value = "index")
    @ResponseBody
    public ModelAndView main(){
        ModelAndView mode= new ModelAndView();
        mode.addObject("name","ddfsdfsd");
        mode.setViewName("index");
        return mode;
    }

    @RequestMapping(value = "home")
    public String index(){
        ModelAndView mode= new ModelAndView();
        mode.addObject("name","ddfsdfsd");
        mode.setViewName("main");
        return "main";
    }


}
