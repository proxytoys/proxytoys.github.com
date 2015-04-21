package com.thoughtworks.proxy;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class FakeTest {
	
	@Test
	public void shouldProtectMultipleThreadAccessFooThreadLocal() throws InterruptedException
	{
		FooThreadLocal foo = new FooThreadLocal();
		StringBuilder value = new StringBuilder();
		Thread thread1 = new Thread(new Runnable1(foo));
		Thread thread2 = new Thread(new Runnable2(foo, thread1, value));
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		assertEquals("ThreadLocal", value.toString());
	}
	
	@Test
	public void shouldNotProtectMultipleThreadAccessFoo() throws InterruptedException
	{
		Foo foo = new Foo();
		StringBuilder value = new StringBuilder();
		Thread thread1 = new Thread(new Runnable1(foo));
		Thread thread2 = new Thread(new Runnable2(foo, thread1, value));
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		assertEquals("NonThreadLocal", value.toString());
	}	

	public class Runnable1 implements Runnable
	{
		Foo foo;
		Runnable1(Foo foo)
		{
			this.foo = foo;
		}

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			foo.setValue(5);
		}
	}
	
	public class Runnable2 implements Runnable
	{
		Foo foo;
		Thread t;
		StringBuilder value;
		Runnable2(Foo foo, Thread t, StringBuilder value)
		{
			this.foo = foo;
			this.t = t;
			this.value = value;
		}

		public void run() {
			foo.setValue(3);
			try {
				Thread.sleep(2000);
				t.join();
			} catch (InterruptedException e) {
			}
			if(foo.getValue() == 3)
			{
				value = value.append("ThreadLocal");
			}
			else if(foo.getValue() == 5)
			{
				value = value.append("NonThreadLocal");
			}
		}
	}
	
	

	public class Foo {
		int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}

	public class FooThreadLocal extends Foo {
		ThreadLocal<Foo> fooThread = new ThreadLocal<Foo>();

		@Override
		public void setValue(int value) {
			Foo foo = new Foo();
			foo.setValue(value);
			fooThread.set(foo);
		}
		
		@Override
		public int getValue()
		{
			return fooThread.get().getValue();
		}
	}
}
