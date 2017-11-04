package org.litejunit.v3.runner;

import org.litejunit.v3.notification.RunListener;
import org.litejunit.v3.notification.RunNotifier;
import org.litejunit.v3.runners.InitializationError;
import org.litejunit.v3.runners.TestClassRunner;
import org.litejunit.v3.runners.TextListener;



/**
 * <code>JUnitCore</code> is a facade for running tests. It supports running JUnit 4 tests, 
 * JUnit 3.8.2 tests, and mixtures. To run tests from the command line, run <code>java org.junit.runner.JUnitCore TestClass1 TestClass2 ...</code>.
 * For one-shot test runs, use the static method <code>runClasses(Class... classes)</code>
 * . If you want to add special listeners,
 * create an instance of <code>JUnitCore</code> first and use it to run the tests.
 * 
 * @see org.junit.runner.Result
 * @see org.junit.runner.notification.RunListener
 * @see org.junit.runner.Request
 */
public class JUnitCore {
	//对listener的进一步抽象
	private RunNotifier notifier;

	/**
	 * Create a new <code>JUnitCore</code> to run tests.
	 */
	public JUnitCore() {
		notifier= new RunNotifier();
	}

    public static void  runClass(Class<?> clz){
    	try {
    		//里面有个TestClassMethodsRunner
			TestClassRunner runner = new TestClassRunner(clz);
			JUnitCore core = new JUnitCore();
			core.addListener(new TextListener());		//监听是用于各个阶段打印控制台的
			Result result = core.run(runner);//TODO 核心 里面本质上是交给TestClassRunner运行
			
		} catch (InitializationError e) {
			
			e.printStackTrace();
		}
    	
    }

	/**
	 * Do not use. Testing purposes only.
	 */
	public Result run(Runner runner) {
		Result result= new Result();
		RunListener listener= result.createListener();//一个内置的监听
		addListener(listener);
		
		try {
			notifier.fireTestRunStarted(runner.getDescription());
			runner.run(notifier);//todo
			notifier.fireTestRunFinished(result);
		} finally {
			removeListener(listener);
		}
		return result;
	}
	
	/**
	 * Add a listener to be notified as the tests run.
	 * @param listener the listener
	 * @see org.junit.runner.notification.RunListener
	 */
	public void addListener(RunListener listener) {
		notifier.addListener(listener);
	}

	/**
	 * Remove a listener.
	 * @param listener the listener to remove
	 */
	public void removeListener(RunListener listener) {
		notifier.removeListener(listener);
	}
}
