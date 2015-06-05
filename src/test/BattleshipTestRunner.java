/**
 * @file TestRunner.java
 * @brief File contains TestRunner for the battleship project.
 * @authors Lars, Rickard
 * @date 2015-05-30
 * */
package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @package test
 * @class TestRunner Runs the test for all classes listed as suiteClasses in <code>BattleshipTestSuite</code>
 * @brief This class is responsible for running the tests and reporting the results.
 * */
public class BattleshipTestRunner {
	/**
	 * @brief Main method for test suite.
	 * */
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(BattleshipTestSuite.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		/**
		 * Battleship test result report.
		 * */
		System.out.println("The JUnitTestSuite class tested "
				+ result.getRunCount()
				+ " methods\nThe tests were successful?: "
				+ result.wasSuccessful() + "\nFailure count: "
				+ result.getFailureCount());
	}
}