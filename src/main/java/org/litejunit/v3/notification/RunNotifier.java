package org.litejunit.v3.notification;

import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * If you write custom runners, you may need to notify JUnit of your progress running tests.
 * Do this by invoking the <code>RunNotifier</code> passed to your implementation of
 * <code>Runner.run(RunNotifier notifier)</code>. Future evolution of this class is likely to 
 * move <code>fireTestRunStarted()</code> and <code>fireTestRunFinished()</code>
 * to a separate class since they should only be called once per run.
 */
//管理所有监听器的 JUnitCore里面包含了一个
public class RunNotifier {
	private List<AbstractRunListener> fListeners= new ArrayList<AbstractRunListener>();

	/** Internal use only
	 */
	public void addListener(AbstractRunListener listener) {
		fListeners.add(listener);
	}

	/** Internal use only
	 */
	public void removeListener(AbstractRunListener listener) {
		fListeners.remove(listener);
	}

	//内部抽象类 模板
	private abstract class AbstractSafeNotifier {
		void run() {
			for (Iterator<AbstractRunListener> allListeners = fListeners.iterator(); allListeners.hasNext();) {

				try {
					//关键
					notifyListener(allListeners.next());
				} catch (Exception e) {
					// Remove the offending listener first to avoid an infinite loop
					allListeners.remove();

					fireTestFailure(new Failure(Description.TEST_MECHANISM, e));
				}

			}
		}
		//等子类自由实现
		abstract protected void notifyListener(AbstractRunListener each) throws Exception;
	}
	

	public void fireTestRunStarted(final Description description) {
		//这个抽象类 只是用来遍历每个监听器的
		new AbstractSafeNotifier() {
			/**
			 *监听器具体干啥
			 * @param listener 目前只有 Result.Listener 内部类 和 TextListener
			 */
			@Override
			protected void notifyListener(AbstractRunListener listener) throws Exception {
				//Result里面只记录下时间  TextListener没有重写这个方法 空的
				listener.testRunStarted(description);
			};
		}.run();//里面会每个监听器调用notifyListener
	}
	
	/**
	 * Do not invoke.
	 */
	public void fireTestRunFinished(final Result result) {
		new AbstractSafeNotifier() {
			@Override
			protected void notifyListener(AbstractRunListener each) throws Exception {
				each.testRunFinished(result);
			};
		}.run();
	}
	
	/**
	 * Invoke to tell listeners that an atomic test is about to start.
	 * @param description the description of the atomic test (generally a class and method name)
	 * @throws StoppedByUserException thrown if a user has requested that the test run stop
	 */
	public void fireTestStarted(final Description description) throws StoppedByUserException {
		boolean fPleaseStop = false;
		if (fPleaseStop) {
			throw new StoppedByUserException();
		}
		new AbstractSafeNotifier() {
			@Override
			protected void notifyListener(AbstractRunListener each) throws Exception {
				each.testStarted(description);
			};
		}.run();
	}

	/**
	 * Invoke to tell listeners that an atomic test failed.
	 * @param failure the description of the test that failed and the exception thrown
	 */
	public void fireTestFailure(final Failure failure) {
		new AbstractSafeNotifier() {
			@Override
			protected void notifyListener(AbstractRunListener listener) throws Exception {
				listener.testFailure(failure);
			};
		}.run();
	}


	/**
	 * Invoke to tell listeners that an atomic test finished. Always invoke <code>fireTestFinished()</code>
	 * if you invoke <code>fireTestStarted()</code> as listeners are likely to expect them to come in pairs.
	 * @param description the description of the test that finished
	 */
	public void fireTestFinished(final Description description) {
		new AbstractSafeNotifier() {
			@Override
			protected void notifyListener(AbstractRunListener each) throws Exception {
				each.testFinished(description);
			};
		}.run();
	}
	

}