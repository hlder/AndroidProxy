# AndroidProxy
java中要实现动态代理，必须要写一个接口，才能创建动态代理。cglib可以实现不需要实现接口就能创建动态代理，但是cglib采用asm技术进行class插装，而Android中是dex所以无法使用。也有人使用dexmaker来进行dex插装，但是总有些不好用。

apt现在很多流行框架都有使用，所以我便用apt技术实现了Android中的动态代理。

## 用法：

### 一、添加依赖

```groovy
implementation project(":proxy")
annotationProcessor project(":proxy-compiler")
//如果使用kotlin加上下面这句话
kapt project(":proxy-compiler")
```

### 二、在需要动态代理的类上加上@Proxy注解，该类必须要有默认构造函数

```java
//java代码
@Proxy
public class TestBean {
    private String name;
    public void setName(String name){
        this.name = name;
    }
    public void say(String message){
        System.out.println(name+" say:"+message);
    }
}
```

```kotlin
//kotlin需要注意，class必须加上open，因为需要被继承
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
```

### 三、使用的时候直接按照下面方法创建就可以了。

```java
void createTestBeanProxy(){
    TestBean testBean= AProxy.create(TestBean.class, new ProxyHandler() {
        @Override
        public Object intercept(Object object, Method method, Object... args) {
            try {
                System.out.println("代理执行:"+" method:"+method.getName()+"  args:"+args);
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
```

