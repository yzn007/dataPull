package com.springboot.httpInterface.entity;

import java.util.Set;

/**
 * Created by yzn00 on 2019/6/24.
 */
//@Data
public class Channel {
    String name;

    public Channel(String name, Set<IMClient> subscriptionSet) {
        this.name = name;
        this.subscriptionSet = subscriptionSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<IMClient> getSubscriptionSet() {
        return subscriptionSet;
    }

    public void setSubscriptionSet(Set<IMClient> subscriptionSet) {
        this.subscriptionSet = subscriptionSet;
    }

    Set<IMClient> subscriptionSet;
}
