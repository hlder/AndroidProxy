package com.hld.proxy;

import java.lang.reflect.Method;
public interface ProxyHandler {
    Object intercept(Object object, Method method, Object ... args) ;
}
