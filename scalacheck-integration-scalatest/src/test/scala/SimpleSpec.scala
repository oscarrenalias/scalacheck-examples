package com.company.scalacheck

import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import org.scalatest.matchers.ShouldMatchers
import org.scalacheck.{Arbitrary, Gen}

/**
 * This is an example spec written in ScalaTest using ScalaCheck property checks.
 *
 * An advantage of using ScalaTest is that we can use its more readable matchers 
 * within our properties, i.e. "should be" instead of "==".
 */
class SimplePropertySpec extends PropSpec with PropertyChecks with ShouldMatchers {
	/**
	 * The simplest property check
	 */
	property("String should append each other with the concat method") {
		forAll { (a:String, b:String) =>
			a.concat(b) should be (a + b)
		}
	}
	
	/**
	 * An example of a failing property. The console reporting looks somewhat different
	 * to how it would look like if plain ScalaCheck was used
	 */
	property("This property should fail") {
		forAll { (a: String, b: String) =>
			a.length + b.length should equal ((a + b).length + 1) // Should fail
		}
	}
	
	/**
	 * ScalaTest's whenever function replaces ScalaCheck's ==> function for conditional.
	 * property values
	 */
	property("Reverse non-empty strings correctly") {
		forAll { (a:String) =>
			whenever(a.length > 0) {
				a.charAt(a.length-1) should be(a.reverse.charAt(0))
			}
		}
	}
}