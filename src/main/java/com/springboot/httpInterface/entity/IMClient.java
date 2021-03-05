package com.springboot.httpInterface.entity;

/**
 * Created by yzn00 on 2019/6/24.
 */
//@Data
public class IMClient {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    String name;
    long saveTime;
    /**
     * 过期时间 ms，<=0即不过期
     */
    int expire;

}
