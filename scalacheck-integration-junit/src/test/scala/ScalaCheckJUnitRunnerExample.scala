package com.company.junit

import org.scalacheck.Prop._
import org.junit.runner.RunWith
import com.company.scalacheck.support.ScalaCheckJUnitPropertiesRunner
import com.company.scalacheck.Rectangle
import org.scalacheck.{Gen, Properties}

/**
 * This is an example of a JUnit test suite implemented as a ScalaCheck Properties object, where all
 * unit test cases are properties that are evaluated as separate JUnit test cases
 *
 * The code is exactly the same as if the property was run using org.scalacheck.Test.check (and in
 * fact it can still be run like that) but using the @RunWith annotation with our custom runner
 * it can also be run as a JUnit suite
 */
@RunWith(classOf[ScalaCheckJUnitPropertiesRunner])
class ScalaCheckRunnerTest extends Properties("Rectangle property suite") {
	import RectangleGenerator._

	// This holds true and will be reported as a passed test by JUnit
	property("Test biggerThan") = forAll { (r1:Rectangle, r2:Rectangle) =>
		(r1 biggerThan r2) == (r1.area > r2.area)
	}

	// This does not hold true and will be reported as a test error by JUnit
	property("Failed test") = forAll {(a:Int) =>
		a == 1
	}

	// This holds true, and ScalaCheck will output the test data grouping to the console
	property("Test with collection of data") = forAll {(a:Int) =>
		(a > 0 && a <= 10) ==> collect(a) {
			2 * a == a + a
		}
	}
}