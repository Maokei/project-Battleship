/**
 * @file BattleshipTestSuite.java
 * @brief File contains a JUnit test suite
 * @author Lars, Rickard
 * @date 2015-05-30
 * */
package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @class Suite 
 * @brief Calculator test suite add reference to JUnit classes attach 
 * @RunWith(Suite.class) annotation with classes add reference to JUnit test classes using 
 * @Suite.SuiteClasses annotation
 * */
@RunWith(Suite.class)
@Suite.SuiteClasses
({ 
	TestShips.class, TestGameMode.class
})

public class BattleshipTestSuite {}
