package com.springboot.common;

import com.springboot.httpInterface.entity.CommonMessage;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yzn00 on 2019/6/24.
 */
public class IMMessageQueue {
    private DeferredResult<List<CommonMessage>> result;
    //使用LinkedList作为消息队列
    private final LinkedList<CommonMessage> messageQueue = new LinkedList<>();

    public synchronized void send(CommonMessage message){
        messageQueue.add(message);
        flush();
    }
    public DeferredResult<List<CommonMessage>> poll(){
        result = new DeferredResult<>(10000L);
        flush();
        result.onTimeout(()->result.setResult(null));
        return result;
    }
    /**
     * flush()方法会在DeferredResult可用（非空且未被使用）时把消息发送出去，在send和poll时都会执行flush(),
     * 这样无论什么情况下消息最终都会被发送出去
     */
    private synchronized void flush(){
        if (result!=null&&!result.hasResult()&&messageQueue.size()>0) {
            //这里需要拷贝一份消息，因为此处为异步调用，而在当前线程中，messageQueue的引用随后将被clear()
            result.setResult(new ArrayList<>(messageQueue));
            messageQueue.clear();
        }
    }

}
