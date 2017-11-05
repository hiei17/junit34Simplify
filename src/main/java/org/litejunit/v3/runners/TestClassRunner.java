package org.litejunit.v3.runners;

import org.litejunit.v3.AfterClass;
import org.litejunit.v3.BeforeClass;
import org.litejunit.v3.notification.Failure;
import org.litejunit.v3.notification.RunNotifier;
import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Runner;

//为了处理@BeforeClass @AfterClass 而存在的 里面包含一个 TestClassMethodsRunner(这个里面才是真的用到测试类
public class TestClassRunner extends Runner  {
	//TestClassMethodsRunner
	protected final Runner enclosedRunner;
	//测试类
	private final Class<?> testClass;

	public TestClassRunner(Class<?> klass)  {
		testClass= klass;
		enclosedRunner= new TestClassMethodsRunner(klass);
	}


	@Override
	public void run(final RunNotifier notifier) {
		//为了解决 测试类的 类前后调用的
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(
				testClass,
				BeforeClass.class,//runUnprotected前调用有这个注解的
				AfterClass.class,//runUnprotected后调用有这个注解的
				null) {

			//方法主体实现
			@Override
			protected void runUnprotected() {
				//  TestClassMethodsRunner
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
