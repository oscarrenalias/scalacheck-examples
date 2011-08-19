package com.company.junit

import org.scalacheck.Prop._
import org.junit.runner.{RunWith, Description}
import org.junit.Test
import org.scalacheck.Test.Params
import org.scalacheck.{ConsoleReporter, Test => SchkTest, Prop, Properties}
import org.junit.runner.notification.{Failure, RunNotifier}
import java.lang.Throwable

/**
 * TODO: seems to be working, but it needs to be cleaned up
 */
class ScalaCheckJUnitPropertiesRunner(suiteClass: java.lang.Class[Properties]) extends org.junit.runner.Runner {
	//private val canInstantiate = Suite.checkForPublicNoArgConstructor(suiteClass)
	//require(canInstantiate, "Must pass an org.scalatest.Suite with a public no-arg constructor")

	private val suiteToRun = suiteClass.newInstance

	/**
	 * Create a description
	 */
	lazy val getDescription = createDescription(suiteToRun)

	private def createDescription(props: Properties): Description = {
		val description = Description.createSuiteDescription(props.name)

		// If we don't add the testNames and nested suites in, we get
		// Unrooted Tests show up in Eclipse
		/*for (name <- suite.testNames) {
			description.addChild(Description.createTestDescription(suite.getClass, name))
		}
		for (nestedSuite <- suite.nestedSuites) {
			description.addChild(createDescription(nestedSuite))
		}*/

		// TODO: we should add the names of the properties within the class

		description
	}

	/**
	 * Run this <code>Suite</code> of tests, reporting results to the passed <code>RunNotifier</code>.
	 * This class's implementation of this method invokes <code>run</code> on an instance of the
	 * <code>suiteClass</code> <code>Class</code> passed to the primary constructor, passing
	 * in a <code>Reporter</code> that forwards to the  <code>RunNotifier</code> passed to this
	 * method as <code>notifier</code>.
	 *
	 * @param notifier the JUnit <code>RunNotifier</code> to which to report the results of executing
	 * this suite of tests
	 */
	private class CustomConsoleReporter extends ConsoleReporter(1)
	private implicit def doCheck(p: Prop): Boolean = SchkTest.check(Params(testCallback = new CustomConsoleReporter), p).passed

	def run(notifier: RunNotifier) {
		//suiteToRun.run(None, new RunNotifierReporter(notifier), new Stopper {}, Filter(), Map(), None, new Tracker) // TODO: What should this Tracker be?

		// TODO: what do we do with RunNotifier?
		//notifier.fireTestStarted(getDescription)
		//doCheck(suiteToRun)


		suiteToRun.properties.map({ propTuple =>
			propTuple match {
				case (desc, prop) => {
					val descObj = Description.createTestDescription(prop.getClass, desc)
					notifier.fireTestStarted(descObj)
					if(!doCheck(prop))
						notifier.fireTestFailure(new Failure(descObj, new Throwable("TODO")))
					notifier.fireTestFinished(descObj)
				}
			}
		})

		//notifier.fireTestFinished(getDescription)
	}

	/**
	 * Returns the number of tests that are expected to run when this ScalaTest <code>Suite</code>
	 * is run.
	 *
	 * @return the expected number of tests that will run when this suite is run
	 */
	override def testCount() = suiteToRun.properties.size
}

@RunWith(classOf[ScalaCheckJUnitPropertiesRunner])
class ScalaCheckRunnerTest extends Properties("Test property suite") {
	property("sum property") = forAll { (a:Int, b:Int) =>
		a + b == a - b
	}

	property("multiply property") = forAll { (a:Int) =>
		a + a == 2*a
	}
}