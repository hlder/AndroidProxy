package com.hld.myandroidproxy.test;

import com.hld.proxy.Proxy;

@Proxy
public class TestBean2 {
    private String name;

    public void setName(String name){
        this.name=name;
    }


    public void say(String message){
        System.out.println(name+" say:"+message);
    }
}
