package com.hld.myandroidproxy.test

import com.hld.proxy.Proxy

@Proxy
open class TestKoltinBean {
    private var name:String?=null


    fun setName(name:String){
        this.name=name
    }

    fun getName():String?{
        return name
    }

    fun say(message:String){
        println("$name say : $message")
    }


}