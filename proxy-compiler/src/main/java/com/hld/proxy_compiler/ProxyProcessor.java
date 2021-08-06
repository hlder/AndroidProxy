package com.hld.proxy_compiler;

import com.google.auto.service.AutoService;
import com.hld.proxy.Proxy;
import com.hld.proxy.ProxyHandler;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.hld.proxy.Proxy"})
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ProxyProcessor extends AbstractProcessor {

//    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
//        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if(set.isEmpty()){
            return false;
        }

        Set<? extends Element> elements=roundEnvironment.getElementsAnnotatedWith(Proxy.class);


        for (Element element:elements){
            Proxy testApt=element.getAnnotation(Proxy.class);
            if(testApt!=null){
                TypeMirror superClassTypeMirror = element.asType();


                List<MethodSpec> listMethods=new ArrayList<>();
                List<? extends Element>  list=element.getEnclosedElements();
                for(Element item:list){


                    boolean isCanOverride=true;
                    for(Modifier modifier:item.getModifiers()){
                        ///如果方法不是public，并且也不是protected，那么这个方法不需要重写
                        if(!"public".equals(modifier.toString())&&!"protected".equals(modifier.toString())){
                            isCanOverride=false;
                        }
                    }

                    if(!isCanOverride){//不可以被重写
                        continue;
                    }

                    String methodName=item.getSimpleName().toString();
                    List<ParameterSpec> listParameters=new ArrayList<>();
                    Type.MethodType methodType=(Type.MethodType)item.asType();


                    StringBuilder superParameters=new StringBuilder();
                    StringBuilder superParameterClass=new StringBuilder();
                    ///设置参数
                    for(int i=0;i<methodType.getParameterTypes().size();i++){
                        Type paramenterType=methodType.getParameterTypes().get(i);
                        ParameterSpec parameterSpec=ParameterSpec.builder(TypeName.get(paramenterType),"arg"+i).build();
                        listParameters.add(parameterSpec);
                        superParameters.append("arg").append(i);

                        superParameterClass.append(TypeName.get(paramenterType)).append(".class");

                        if(i<(methodType.getParameterTypes().size()-1)){
                            superParameters.append(",");
                            superParameterClass.append(",");
                        }

                    }

                    //返回值类型
                    TypeName returnType=TypeName.get(methodType.getReturnType());



                    MethodSpec.Builder methodBuilder=MethodSpec.methodBuilder(methodName);
                    methodBuilder.addModifiers(item.getModifiers());


//                    private CodeBlock proxyCodeBlock(String returnTypeStr,String methodName,String argsClassStr,String argsStr){

                    CodeBlock.Builder codeBlock=proxyCodeBlock(superClassTypeMirror.toString(),returnType.toString(),methodName,superParameterClass.toString(),superParameters.toString());

                    if(!TypeName.VOID.equals(returnType)){//表示有返回值
                        methodBuilder.returns(returnType);

                        methodBuilder.addStatement(codeBlock.build());
                        //程序末尾需要有return
                        methodBuilder.addStatement("return super."+methodName+"("+superParameters+")");
                    }else if("<init>".equals(methodName)){//构造函数
                        methodBuilder.addStatement("super("+superParameters+")");
                    }else{//void方法
                        codeBlock.add(CodeBlock.of("else super."+methodName+"("+superParameters+")"));

                        methodBuilder.addStatement(codeBlock.build());
//                        methodBuilder.addStatement("else super."+methodName+"("+superParameters+")");
                    }

                    methodBuilder.addParameters(listParameters);

                    listMethods.add(methodBuilder.build());
                }




                MethodSpec _setAndroidProxy = MethodSpec.methodBuilder("_112233445566778899setAndroidProxy")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addParameter(ProxyHandler.class,"_proxyHandler")
                        .addStatement("this._proxyHandler=_proxyHandler")
                        .build();
                listMethods.add(_setAndroidProxy);


                CodeBlock.Builder codeBlock = CodeBlock.builder();
                codeBlock.add(CodeBlock.of("this._proxyParent=new "+element.getSimpleName()+"();\n"));

                FieldSpec _proxyParent=FieldSpec.builder(TypeName.get(superClassTypeMirror),"_proxyParent",Modifier.PRIVATE).build();

                FieldSpec _proxyHandler=FieldSpec.builder(ProxyHandler.class,"_proxyHandler",Modifier.PRIVATE).build();

                //创建类
                TypeSpec helloWorld = TypeSpec.classBuilder( element.getSimpleName()+"_112233445566778899Proxy")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .superclass(superClassTypeMirror)
                        .addMethods(listMethods)
                        .addInitializerBlock(codeBlock.build())
                        .addField(_proxyParent)
                        .addField(_proxyHandler)
                        .build();

                String packageName=element.asType().toString().replace("."+element.getSimpleName(),"");
//                //创建包
                JavaFile javaFile = JavaFile.builder(packageName, helloWorld).build();
                try {
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //1.先拿到class,和包名
                //2.创建class继承上面的class
                //3.重写父类class中的所有方法，并增加调用代理方法的代码
                //4.创建代码

            }
        }

        return true;
    }



    private CodeBlock.Builder proxyCodeBlock(String parentClassName,String returnTypeStr,String methodName,String argsClassStr,String argsStr){
        CodeBlock.Builder codeBlock = CodeBlock.builder();

        codeBlock.add(CodeBlock.of("if(_proxyHandler!= null) {\n"));
        codeBlock.add(CodeBlock.of("try {\n"));

        if(argsStr!=null&&argsStr.length()>0){
            argsStr=","+argsStr;
        }
        if(argsClassStr!=null&&argsClassStr.length()>0){
            argsClassStr=","+argsClassStr;
        }


        if(!TypeName.VOID.toString().equals(returnTypeStr)){
            codeBlock.add(CodeBlock.of("return ("+returnTypeStr+")_proxyHandler.intercept(_proxyParent, "+parentClassName+".class.getMethod(\""+methodName+"\""+argsClassStr+")"+argsStr+");\n"));
        }else{
            codeBlock.add(CodeBlock.of("_proxyHandler.intercept(_proxyParent, "+parentClassName+".class.getMethod(\""+methodName+"\""+argsClassStr+")"+argsStr+");\n"));
        }

        codeBlock.add(CodeBlock.of("} catch (NoSuchMethodException | SecurityException e) {\n"));
        codeBlock.add(CodeBlock.of("e.printStackTrace();\n"));
        codeBlock.add(CodeBlock.of("}\n"));
        codeBlock.add(CodeBlock.of("}\n"));


//        codeBlock.add(CodeBlock.of(""));

        return codeBlock;

    }
}
