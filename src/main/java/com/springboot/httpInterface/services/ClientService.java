package com.springboot.httpInterface.services;

import com.springboot.httpInterface.entity.IMClient;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yzn00 on 2019/6/24.
 */
@Service
public class ClientService {
    private final int expire = 600 * 1000;//user的过期时间 600s没有活动 频道中的user自动过期

    /**
     * 过期时间间隔
     * @return 过期时间间隔(ms)
     */
    public int getExpire(){
        return expire;
    }
    /**
     * 获取默认过期时间的IMClient
     * @param session httpSession
     */
    public IMClient getIMClient(HttpSession session){
        return getIMClient(session,this.expire);
    }

    /**
     * 获取指定过期时间的IMClient
     * @param session httpSession
     * @param expire 过期时间，<=0代表不会过期
     */
    private IMClient getIMClient(HttpSession session, int expire){
        IMClient client =(IMClient)session.getAttribute("imUser");
        if(client != null&&client.getSaveTime()<= new Date().getTime()){//如果过期 则从session中删除用户
            session.removeAttribute("imUser");
            client = null;
        }
        if (client == null) {
            client = new IMClient();//测试时client的作用仅仅是作为map的key所以new一个即可
            client.setId(UUID.randomUUID().toString());
            client.setSaveTime(new Date().getTime()+expire);//设置过期时间
            session.setAttribute("imUser",client);
        }
        client.setExpire(expire);
        return client;
    }

    public boolean isExpired(IMClient client) {
        return client.getExpire() > 0 && client.getSaveTime() <= new Date().getTime();
    }

}
