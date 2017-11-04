package org.litejunit.v3.runners;

import org.litejunit.v3.After;
import org.litejunit.v3.Before;
import org.litejunit.v3.notification.Failure;
import org.litejunit.v3.notification.RunNotifier;
import org.litejunit.v3.runner.Description;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


//一个测试方法对应一个这个
public class TestMethodRunner extends BeforeAndAfterRunner {
	private final Object test;
	private final Method method;
	private final RunNotifier notifier;
	private final Description description;

	public TestMethodRunner(Object test, Method method, RunNotifier notifier, Description description) {
		super(test.getClass(), Before.class, After.class, test);
		this.test= test;
		this.method= method;
		this.notifier= notifier;
		this.description= description;
	}

	public void run() {

		notifier.fireTestStarted(description);//运行前 监听
		try {
			//todo 里面包含 before after
			runProtected();
		} finally {
			//@After
			notifier.fireTestFinished(description);//运行后 监听
		}
	}


	@Override
	protected void runUnprotected() {
		try {
			method.invoke(test);

		} catch (InvocationTargetException e) {
			addFailure(e);

		} catch (Throwable e) {
			addFailure(e);
		}
	}

	@Override
	protected void addFailure(Throwable e) {
		notifier.fireTestFailure(new Failure(description, e));
	}
	

}

