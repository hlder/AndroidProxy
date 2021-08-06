package com.hld.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AProxy {

    public static<T> T create (Class<T> cls,ProxyHandler handler){

        //获取代理类的类名，注解处理器会在编译时，将带有@Proxy注解的类，在同一目录下，创建一个代理类的子类，子类的名字会被加上_112233445566778899Proxy
        String proxyClsName = cls.getPackage().getName()+"."
                + cls.getSimpleName()+"_112233445566778899Proxy";
        try {
            Class<?> proxyClass=Class.forName(proxyClsName);//加载代理类的class
            T newObj=(T) proxyClass.getDeclaredConstructor().newInstance();//创建代理类对象，必须有默认构造函数

            //获取代理类的setAndroidProxy方法
            Method method=proxyClass.getMethod("_112233445566778899setAndroidProxy",ProxyHandler.class);
            //把handler设置到代理对象中，当调用代理对象中的方法时，就会先执行handler中的方法。
            method.invoke(newObj,handler);
            return newObj;
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return null;
    }

}
