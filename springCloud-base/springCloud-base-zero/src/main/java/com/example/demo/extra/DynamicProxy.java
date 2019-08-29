package com.example.demo.extra;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理
 * InvocationHandler   JDK动态代理接口，原理反射
 * MethodInterceptor   CGLIB动态代理类，原理ASM框架修改字节码
 */
public class DynamicProxy implements InvocationHandler, MethodInterceptor {
    //测试
    public static void main(String[] args) {
        DynamicProxy dynamicProxy = new DynamicProxy();

        //JDK
        System.out.println("JDK Proxy");
        Iproxy iproxy = (Iproxy) dynamicProxy.bind(new ProxyImpl());
        iproxy.testProxy();
        Iproxy2 iproxy2 = (Iproxy2) dynamicProxy.bind(new ProxyImpl());
        iproxy2.testProxy2();

        //CGLIB
        System.out.println("\nCGLIB Proxy");
        Iproxy iproxyT = (Iproxy) dynamicProxy.enhancer(new ProxyImpl());
        iproxyT.testProxy();
        Iproxy2 iproxy2T = (Iproxy2) dynamicProxy.enhancer(new ProxyImpl());
        iproxy2T.testProxy2();
    }

    private Object obj;

    public DynamicProxy() {
    }

    /**
     * 绑定委托对象，并返回代理类(JDK)
     */
    public Object bind(Object o) {
        this.obj = o;
        return Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(), this);
    }

    /**
     * 方法调用(JDK)
     */
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        System.out.println("------ JDK start");
        Object invoke = method.invoke(obj, args);
        System.out.println("------ JDK end");
        return invoke;
    }

    /**
     * 绑定委托对象，并返回代理类(CGLIB)
     */
    public Object enhancer(Object o){
        this.obj = o;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ProxyImpl.class);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     * 方法调用(CGLIB)
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("------ CGLIB proxy start");
        /*
            代理方法.invokeSuper(是代理后的子类,args) 调用的对象是增强的，如果在方法里面调用其他方法，会再走一遍MethodInterceptor方法
         */
//        Object invoke = proxy.invokeSuper(o, args);
        /*
            调用方法.invoke(子类,args) 调用的对象是没有增强的，如果在方法里面调用其他方法，不会再走MethodInterceptor方法
            target对象存的是原生的bean，没有被CGLIB代理的对象，所以就无法实现自身调用增强；
         */
        Object invoke = method.invoke(obj, args);
        System.out.println("------ CGLIB proxy end");
        return invoke;
    }

}

interface Iproxy {
    void testProxy();
}

interface Iproxy2 {
    void testProxy2();
}

class ProxyImpl implements Iproxy, Iproxy2 {
    @Override
    public void testProxy() {
        System.out.println("-------- testProxy -------");
        t();
    }

    @Override
    public void testProxy2() {
        System.out.println("-------- testProxy2 -------");
    }

    public void t(){
        System.out.println("--- test CGLIB Enhance ---");
    }
}

