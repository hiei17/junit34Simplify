package org.litejunit.v3.runners;

import org.litejunit.v3.AfterClass;
import org.litejunit.v3.BeforeClass;
import org.litejunit.v3.notification.Failure;
import org.litejunit.v3.notification.RunNotifier;
import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Runner;

//为了处理@BeforeClass @AfterClass 而存在的 里面包含一个 TestClassMethodsRunner(这个里面才是真的用到测试类
public class TestClassRunner extends Runner  {
	protected final Runner enclosedRunner;
	//测试类
	private final Class<?> testClass;

	public TestClassRunner(Class<?> klass) throws InitializationError {
		//todo
		this(klass, new TestClassMethodsRunner(klass));
	}
	
	public TestClassRunner(Class<?> klass, Runner runner) throws InitializationError {
		testClass= klass;
		enclosedRunner= runner;
		
	}

	

	@Override
	public void run(final RunNotifier notifier) {
		//为了解决测试类 类前后调用的
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(
				testClass,
				BeforeClass.class,
				AfterClass.class,
				null)
		{
			//方法主体实现
			@Override
			protected void runUnprotected() {
				//todo  TestClassMethodsRunner
				enclosedRunner.run(notifier);
			}
		
			// 测试失败调用方法
			@Override
			protected void addFailure(Throwable targetException) {
				notifier.fireTestFailure(new Failure(getDescription(), targetException));
			}
		};

		runner.runProtected();
	}

	@Override
	public Description getDescription() {
		return enclosedRunner.getDescription();
	}

}
