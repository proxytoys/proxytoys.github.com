package com.thoughtworks.proxy.gn;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultipleThreadTest {
    class Foo {

        private int bar = 0;

        public void setBar(int bar) {
            this.bar = ++bar;
            journal(" setBar:" + bar);
        }

        public int getBar() {
            journal(" getBar=" + bar);
            return this.bar;
        }
    }

    class FooThreadLocalized extends Foo {

       private ThreadLocal<Foo> tlFoo = new ThreadLocal<Foo>();

       public void setBar(int bar) {
            get().setBar(bar);
       }
       public int getBar() {
         return get().getBar();
       }

       private Foo get() {
         Foo foo = tlFoo.get();
         if (foo == null) {
             foo = new Foo();
             tlFoo.set(foo);
         }
           return foo;
       }

    }

    private final static StringBuilder SB = new StringBuilder();

    public static void journal(String message) {
        synchronized (SB) {
            SB.append(message);
        }
    }


    @Test
    public void threadLocalizedSubclassShouldOverrideStateOfTestBaseClass() throws Exception {

        doIt(new FooThreadLocalized());
        assertEquals("setBar:2 setBar:4 getBar=2 getBar=4", SB.toString().trim());

    }

    private void doIt(Foo foo) throws InterruptedException {
        SB.delete(0, SB.length());
        Thread thread1 = new Thread(new RunnableOne(foo));
        Thread thread2 = new Thread(new RunnableTwo(foo));
        thread1.start();
        thread2.start();
        Thread.sleep(2000);
    }

    @Test
    public void lackOfThreadLocalizingShouldBeAbleToMutateCommonClass() throws Exception {

        doIt(new Foo());
        // This illustrates failure to have thread localized Foo instances per thread.
        assertEquals("setBar:2 setBar:4 getBar=4 var not 2!!! getBar=4", SB.toString().trim());

    }

    private static class RunnableOne implements Runnable {
        private final Foo foo;

        public RunnableOne(Foo foo) {
            this.foo = foo;
        }

        public void run() {
            foo.setBar(1);
            sleep(100);
            int var = foo.getBar();
            if (var != 2) {
                journal(" var not 2!!!");
            }

        }

    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    private static class RunnableTwo implements Runnable {
        private final Foo foo;

        public RunnableTwo(Foo foo) {
            this.foo = foo;
        }

        public void run() {
            sleep(50);
            foo.setBar(3);
            sleep(100);
            int var = foo.getBar();
            if (var != 4) {
                journal(" var not 4!!!");
            }

        }
    }
}
