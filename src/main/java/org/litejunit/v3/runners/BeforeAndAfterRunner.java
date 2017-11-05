package org.litejunit.v3.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class BeforeAndAfterRunner {

	//异常类就用个名字
	private static class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}

	//注解是哪个 是子类实现的构造方法里面穿的
	private final Class<? extends Annotation> beforeAnnotation;
	private final Class<? extends Annotation> afterAnnotation;

	//指定注解方法查找器
	private TestIntrospector testIntrospector;

	//测试类
	private Object test;

	public BeforeAndAfterRunner(Class<?> testClass,
			Class<? extends Annotation> beforeAnnotation,
			Class<? extends Annotation> afterAnnotation, 
			Object test) {

		this.beforeAnnotation= beforeAnnotation;
		this.afterAnnotation= afterAnnotation;
		this.testIntrospector= new TestIntrospector(testClass);
		this.test= test;
	}

	//TestMethodRunner 或者 TestClassRunner 的 run()里面调用
	public void runProtected() {
		try {
			//@BeforeClass //@Before
			runBefores();

			//todo 主体方法 留给子类实现
			runUnprotected();
		} catch (FailedBefore e) {
			;
		} finally {
			//@AfterClass ////@After
			runAfters();
		}
	}

	//得子类实现
	protected abstract void runUnprotected();
	protected abstract void addFailure(Throwable targetException);

	// Stop after first failed @Before
	private void runBefores() throws FailedBefore {
		try {
			List<Method> befores= testIntrospector.getTestMethods(beforeAnnotation);
			for (Method before : befores) {
				before.invoke(test);
			}
		} catch (InvocationTargetException e) {
			addFailure(e.getTargetException());
			throw new FailedBefore();
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	//@Afters regardless
	private void runAfters() {
		List<Method> afters= testIntrospector.getTestMethods(afterAnnotation);
		for (Method after : afters) {
			try {
				after.invoke(test);
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // Untested, but seems impossible
			}
		}

	}

}
