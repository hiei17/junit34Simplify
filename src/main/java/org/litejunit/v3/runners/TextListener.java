package org.litejunit.v3.runners;

import org.litejunit.v3.notification.AbstractRunListener;
import org.litejunit.v3.notification.Failure;
import org.litejunit.v3.runner.Description;
import org.litejunit.v3.runner.Result;

import java.io.PrintStream;



public class TextListener extends AbstractRunListener {

	private final PrintStream writer=System.out;


	@Override
	public void testRunStarted(Description description) throws Exception {

	}

	@Override
	public void testRunFinished(Result result) {
		writer.println();
		writer.println("Time: " + result.getRunTime() / 1000 );
		printFailures(result);
		printFooter(result);
	}

	@Override
	public void testStarted(Description description) {
		writer.append('.');
	}

	@Override
	public void testFinished(Description description) throws Exception {

	}

	@Override
	public void testFailure(Failure failure) {
		writer.append('E');
	}
	

	
	/*
	 * Internal methods
	 */

	protected void printFailures(Result result) {
		if (result.getFailureCount() == 0)
			return;

		writer.println( result.getFailureCount() + " failure:");


		int i= 1;
		for (Failure each : result.getFailures()) {
			int count = i++;
			writer.println(count + ") " + each.getTestHeader());
			writer.print(each.getTrace());
		}
	}

	protected void printFooter(Result result) {
		if (result.wasSuccessful()) {
			writer.println();
			writer.print("OK");
			writer.println(" (" + result.getRunCount() + " test" + (result.getRunCount() == 1 ? "" : "s") + ")");

		} else {
			writer.println();
			writer.println("FAILURES!!!");
			writer.println("Tests run: " + result.getRunCount() + ",  Failures: " + result.getFailureCount());
		}
		writer.println();
	}

}
