package com.hld.myandroidproxy;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hld.myandroidproxy.test.TestBean;
import com.hld.myandroidproxy.test.TestBean2;
import com.hld.proxy.AProxy;
import com.hld.proxy.ProxyHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


    }

    public void onClick(View view) {
        if(view.getId()==R.id.button1){
            showStr();
        }else if(view.getId()==R.id.button2){
            getStr();
        }else if(view.getId()==R.id.button3){
            testBean2();
        }
    }


    void testBean2(){
        TestBean2 testBean= AProxy.create(TestBean2.class, new ProxyHandler() {
            @Override
            public Object intercept(Object object, Method method, Object... args) {
                try {
                    System.out.println("代理拦截:"+" method:"+method.getName()+"  args:"+args);
                    return method.invoke(object,args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        testBean.setName("张三");

        testBean.say("你好~");
    }
    void getStr(){
        TestBean testBean= AProxy.create(TestBean.class, new ProxyHandler() {
            @Override
            public Object intercept(Object object, Method method, Object... args) {
                try {
                    return method.invoke(object,args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        String str=testBean.getStr("王五",2);
        System.out.println("====str:"+str);

    }

    void showStr(){

        TestBean testBean= AProxy.create(TestBean.class, new ProxyHandler() {
            @Override
            public Object intercept(Object object, Method method, Object... args) {
                try {
                    return method.invoke(object,args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        testBean.show("aasdasd");
    }
}
