package com.springboot.common;

import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzn00 on 2019/6/20.
 */
public class Ret extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 1L;

    public Ret() {
        put("status", "OK");
    }

    public static Ret error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static Ret error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static Ret error(int code, String msg) {
        Ret Ret = new Ret();
        Ret.put("status", msg);
        return Ret;
    }

    public static Ret ok(String msg) {
        Ret Ret = new Ret();
        Ret.put("status", msg);
        return Ret;
    }

    public static Ret ok(Map<String, Object> map) {
        Ret Ret = new Ret();
        Ret.putAll(map);
        return Ret;
    }

    public static Ret ok(String json,String code) {
        Ret Ret = new Ret();
        Ret.put("status",code);
        Ret.put("results",json);
        return Ret;
    }

    public static Ret ok() {
        return new Ret();
    }

    public Ret put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
