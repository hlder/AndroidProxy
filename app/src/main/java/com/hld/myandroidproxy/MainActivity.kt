package com.hld.myandroidproxy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hld.myandroidproxy.test.TestKoltinBean
import com.hld.proxy.AProxy
import com.hld.proxy.ProxyHandler
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() {
    lateinit var testKoltinBean: TestKoltinBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        testKoltinBean= AProxy.create(TestKoltinBean::class.java,
            ProxyHandler { `object`, method, args ->
                try {
                    println("代理拦截:" + " method:" + method.name + "  args:" + args)
                    return@ProxyHandler method.invoke(`object`, *args)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
                null
            })

    }

    fun onClick(view: View) {
        if (view.id == R.id.button1) {
            testKoltinBean.setName("张三")
            testKoltinBean.say("你好~")
        } else if (view.id == R.id.button2) {
            var name=testKoltinBean.getName()
            println("=========name:$name")
        }
    }


}