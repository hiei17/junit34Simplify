package org.litejunit.v3.notification;

import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Result;


public abstract class AbstractRunListener {

	/**
	 * Called before any tests have been run.
	 * @param description describes the tests to be run
	 */
	public abstract void testRunStarted(Description description) throws Exception ;
	/**
	 * Called when all tests have finished
	 * @param result the summary of the test run, including all the tests that failed
	 */
	public abstract void testRunFinished(Result result) throws Exception ;
	
	/**
	 * Called when an atomic test is about to be started.
	 * @param description the description of the test that is about to be run (generally a class and method name)
	 */
	public abstract void testStarted(Description description) throws Exception ;

	/**
	 * Called when an atomic test has finished, whether the test succeeds or fails.
	 * @param description the description of the test that just ran
	 */
	public abstract void testFinished(Description description) throws Exception ;

	/** 
	 * Called when an atomic test fails.
	 * @param failure describes the test that failed and the exception that was thrown
	 */
	public abstract void testFailure(Failure failure) throws Exception ;



}


