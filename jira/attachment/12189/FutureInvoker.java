package com.thoughtworks.proxy.toys.future;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;
import com.thoughtworks.proxy.toys.hotswap.Swappable;
import com.thoughtworks.proxy.toys.nullobject.Null;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class FutureInvoker implements Invoker {
    private final Object target;
    private final ProxyFactory proxyFactory;
    private final Executor executor;

    public FutureInvoker(Object target, ProxyFactory proxyFactory, Executor executor) {
        this.target = target;
        this.proxyFactory = proxyFactory;
        this.executor = executor;
    }

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Class returnType = method.getReturnType();
        Object result = null;
        if (!returnType.equals(void.class)) {
            Object nullResult = Null.object(returnType, proxyFactory);
            final Swappable swappableResult = (Swappable) HotSwapping.object(returnType, proxyFactory, nullResult);
            result = swappableResult;
            Callable callable = new Callable() {
                public Object call() throws IllegalAccessException, InvocationTargetException {
                    Object invocationResult = method.invoke(target, args);
                    swappableResult.hotswap(invocationResult);
                    return swappableResult;
                }
            };
            Executors.execute(executor, callable);
        }

        return result;
    }
}