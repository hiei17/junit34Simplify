package org.litejunit.v3.runner;

import org.litejunit.v3.notification.AbstractRunListener;
import org.litejunit.v3.notification.RunNotifier;
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
	private RunNotifier notifier=new RunNotifier();

	//用这个静态方法开始
    public static void  runClass(Class<?> clz){
    	try {

			JUnitCore core = new JUnitCore();//本类

			core.notifier.addListener(new TextListener());

			//TODO 核心 里面本质上是交给TestClassRunner运行
			Result result = core.run(new TestClassRunner(clz));//TestClassRunner里面包了一个TestClassMethodsRunner

		} catch (Exception e) {
			
			e.printStackTrace();
		}
    	
    }

	/**
	 * 真的运行
	 * @param runner TestClassRunner
	 * @return 运行结果类
	 */
	public Result run(Runner runner) {
    	//总结测试结果的类
		Result result= new Result();

		AbstractRunListener listener= result.createListener();//Result内部类 一个内置的监听

		notifier.addListener(listener);

		try {
			//辗转调用TestClassMethodsRunner.methodDescription 得到整理过的测试类信息
			Description description = runner.getDescription();
			notifier.fireTestRunStarted(description);//通知所有签字监听器 具体干啥看它们自己

			//TestClassRunner
			runner.run(notifier);//todo

			notifier.fireTestRunFinished(result);
		} finally {
			removeListener(listener);
		}
		return result;
	}

	/**
	 * Remove a listener.
	 * @param listener the listener to remove
	 */
	public void removeListener(AbstractRunListener listener) {
		notifier.removeListener(listener);
	}
}
