package org.litejunit.v3.runners;

import org.litejunit.v3.Test;
import org.litejunit.v3.notification.RunNotifier;
import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Runner;

import java.lang.reflect.Method;
import java.util.List;


//测试类所有的方法
public class TestClassMethodsRunner extends Runner  {
	//类里面有@test的方法
	private final List<Method> testMethods;
	//测试类
	private final Class<?> testClass;

	public TestClassMethodsRunner(Class<?> klass) {
		testClass= klass;
		testMethods= new TestIntrospector(testClass).getTestMethods(Test.class);
	}
	
	@Override
	public void run(RunNotifier notifier) {//TODO

		for (Method method : testMethods) {
			//里面会捕捉异常 一个失败不会影响下一个
			invokeTestMethod(method, notifier);//转给TestMethodRunner 执行
		}
	}

	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		Object test;
		try {
			//测试类实例
			test= createTest();

			Description methodDescription = methodDescription(method);

			TestMethodRunner testMethodRunner = new TestMethodRunner(test, method, notifier, methodDescription);
			testMethodRunner.run();
		} catch (Exception e) {
			;
		}

	}


	//类描述 用于监听的打印
	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(testClass.getName());
		List<Method> testMethods= this.testMethods;
		for (Method method : testMethods) {
			spec.addChild(methodDescription(method));
		}
		return spec;
	}

	protected Object createTest() throws Exception {
		return testClass.getConstructor().newInstance();
	}


	protected Description methodDescription(Method method) {
		return Description.createTestDescription(testClass, method.getName());
	}


}