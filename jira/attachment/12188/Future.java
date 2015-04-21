package com.thoughtworks.proxy.toys.future;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.multicast.ClassHierarchyIntrospector;

import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class Future {
    public static Object object(Object target) {
        return object(target, new StandardProxyFactory());
    }

    public static Object object(Object target, ProxyFactory proxyFactory) {
        Class[] types;
        Class targetClass = target.getClass();
        if(proxyFactory.canProxy(targetClass)) {
            types = new Class[]{targetClass};
        } else {
            types = ClassHierarchyIntrospector.getAllInterfaces(targetClass);
        }
        return object(types, target, proxyFactory, Executors.newCachedThreadPool());
    }

    public static Object object(Class[] types, Object target, ProxyFactory proxyFactory, Executor executor) {
        FutureInvoker invoker = new FutureInvoker(target, proxyFactory, executor);
        return proxyFactory.createProxy(types, invoker);
    }
}