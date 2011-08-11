package com.company.scalacheck

import org.scalacheck.Prop._
import org.scalacheck.Properties
import java.lang.Math

/**
 * Grouping of properties into a single specification object
 */
object BiggerSpecification extends Properties("A bigger test specification") {
	property("testList") = forAll { (l1: List[Int], l2: List[Int]) =>
		l1.size + l2.size == (l1 ::: l2).size
	}

	property("Check concatenated string") = forAll { (a:String, b:String) =>
		a.concat(b) == a + b
	}
}

object SecondSpecification extends Properties("A second specification") {
	// TODO: not a very original example, just like the other ones...
	property("Check string length") = forAll { n: Int =>
		(n >= 0 && n < 10000) ==> (List.make(n, "").length == n)
	}
}

object GroupedSpecifications extends Properties("AllApplicationSpecifications") {
	include(BiggerSpecification)
	include(SecondSpecification)
}

/**
 * Used for running the tests from the command line, as there are no JUnit
 * compatible runners for ScalaCheck (could be done if ScalaCheck is used
 * from ScalaTest
 */
object SimpleProperties {

	def run = {

		// a very simple property
		val workingProperty = forAll { (l1: List[Int], l2: List[Int]) =>
			l1.size + l2.size == (l1 ::: l2).size
		}

		// this one fails for negative numbers
		val failingProperty = forAll {(n:Double) =>
			Math.sqrt((n*2)) == n
		}

		// this is the correct version of the property aobve
		val validSqrtProperty = forAll {(n:Double) =>
			(n > 0) ==> (Math.sqrt((n*2)) == n)
		}

		// property combination
		val combinedProperty1 = workingProperty && failingProperty
		val combinedProperty2 = atLeastOne(workingProperty, failingProperty)

		// run the properties and grouped specifications
		workingProperty.check		// holds
		failingProperty.check		// fails
		validSqrtProperty.check		// holds
		combinedProperty1.check 	// fails
		combinedProperty2.check 	// holds
	}
}