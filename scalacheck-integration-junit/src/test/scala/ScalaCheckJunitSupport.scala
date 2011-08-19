package com.company.junit

import org.scalacheck.Test.Params
import org.scalacheck.{Properties, Test => SchkTest, Prop, ConsoleReporter}
import org.junit.Assert._
import org.scalacheck.Prop._
import org.junit.Test

/**
 * can be mixed into any class to provide a doCheck method that can be used to run a ScalaCheck property. The
 * method returns True or False depending on whether the given property holds true or not
 */
trait ScalaCheckJUnitSupport {
	// by default this is a verbose console reporter, so that we can see ScalaCheck's output
	// in the console
	private class CustomConsoleReporter extends ConsoleReporter(1)
	implicit def doCheck(p: Prop): Boolean = SchkTest.check(Params(testCallback = new CustomConsoleReporter), p).passed
}

/**
 * This trait can only be mixed into classes implementing ScalaCheck's Properties class, and takes care
 * of automatically running all properties in the class as defined via Properties.property()
 */
trait ScalaCheckJUnitAdapter extends ScalaCheckJUnitSupport {
	self: Properties =>

	@Test def runAllProperties = {
		System.out.println("==== \nRunning property: " + name)
		assertTrue("Property did not hold true", this)
	}
}

class ScalaJUnitExtendedTest extends Properties("whatever") with ScalaCheckJUnitAdapter {
	property("valid property") = forAll { (a:String) =>
		a == a
	}

	property("failed property") = forAll { (a:String) =>
		// classify input strings based on their length, and only for non-empty strings
		// shorter than 10 characters
		(a.length > 0 && a.length <= 10) ==> collect(a.length) {
			(a != "b")
		}
	}
}