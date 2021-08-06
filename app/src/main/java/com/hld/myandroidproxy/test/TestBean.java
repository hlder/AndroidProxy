package com.hld.myandroidproxy.test;

import com.hld.proxy.Proxy;

/**
 * 使用@Proxy表示可以创建一个代理对象
 */
@Proxy
public class TestBean {
    public TestBean(){

    }
    TestBean(String str){

    }
    private void b(){

    }
    public void a(){
        System.out.println("aaaaaaa");
    }

    public void show(){
        System.out.println("=====show");
    }

    public void show(String str){
        System.out.println("=====show:"+str);
    }

    public String getStr(){
        return "张三";
    }
    public String getStr(String str,int a){
        return a+"张三"+str;
    }

    public TestBean getTestBean(TestBean testBean){
        return testBean;
    }

}
