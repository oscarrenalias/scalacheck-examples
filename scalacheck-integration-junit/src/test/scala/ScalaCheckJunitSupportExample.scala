package com.company.junit

import org.scalacheck.Properties
import org.scalacheck.Prop._
import com.company.scalacheck.support.ScalaCheckJUnitAdapter

/**
 * This is an example of integrating ScalaCheck with JUnit using a custom adapter,
 * where the ScalaCheckJUnitAdapter provides a JUnit test case that will run all our
 * properties as a JUnit test case.
 *
 * The code is exactly the same as for vanilla ScalaCheck properties except for mixing in
 * the ScalaCheckJUnitAdapter into our class to automatically add JUnit integration
 *
 * The drawback of using this kind of integration compared to a native ScalaCheck runner
 * for JUnit is that all properties are run from a single test case.
 */
class ScalaJUnitSupportTest extends Properties("JUnit Support Example") with ScalaCheckJUnitAdapter {
	property("valid property") = forAll { (a:String) =>
		a == a
	}

	property("failed property") = forAll { (a:String) =>
		// this example classifies input strings based on their length, and only for non-empty strings
		// shorter than 10 characters
		(a.length > 0 && a.length <= 10) ==> collect(a.length) {
			(a != "b")
		}
	}
}