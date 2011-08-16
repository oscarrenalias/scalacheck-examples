package com.company.scalacheck

import org.scalatest.PropSpec
import org.scalatest.prop.PropertyChecks
import org.scalatest.prop.Checkers
import org.scalatest.matchers.MustMatchers
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.forAll

/**
 * Case class that we're going to test
 */
case class Rectangle(val width:Double, val height:Double) {
	lazy val area = width * height
	lazy val perimeter = (2*width) + (2*height)
	def biggerThan(r:Rectangle) = (area > r.area)
}

/**
 * The generator and arbitray objects that generate entities of our case class are
 * exactly the same as when using ScalaCheck
 */
object RectangleGenerator {
	// generator for the Rectangle case class
	val rectangleGen:Gen[Rectangle] = for {
		height <- Gen.choose(0,9999)
		width <- Gen.choose(0,9999)
	} yield(Rectangle(width, height))

	// Arbitrary generator of rectangles
	implicit val arbRectangle: Arbitrary[Rectangle] = Arbitrary(rectangleGen)
}

/**
 * This is a ScalaTest specification that tests a case class with ScalaCheck properties. This
 * spec is using the ScalaTest style of property tests (by mixing in the PropertyChecks) trait;
 * it is also possible to use ScalaCheck style property checks by importing the Checkers trait
 * instead. Please note that the forAll method below is provided by PropertyChecks and it's not
 * ScalaCheck's!
 *
 * The main difference is that we can use ScalaTest's matchers (using the MustMatchers
 * trait in this case).
 */
class ArbitraryRectangleSpec extends PropSpec with PropertyChecks with MustMatchers {
	import com.company.scalacheck.RectangleGenerator._

	property("A rectangle should correctly calculate its area") { 
		forAll { (r:Rectangle) =>
			r.area must be (r.width * r.height)
		}
	}
	property("A rectangle should be able to identify which rectangle is bigger") {
		forAll { (r1:Rectangle, r2:Rectangle) =>
			(r1 biggerThan r2) must be(r1.area > r2.area)
		}
	}
}

/**
 * This is the same specification as described above, but written using 
 * ScalaCheck style property checks via the Checkers trait
 */
class ArbitraryRectangleWithCheckersSpec extends PropSpec with Checkers {
	import com.company.scalacheck.RectangleGenerator._
	
	property("A rectangle should correctly calculate its area") { 
		check(forAll { (r:Rectangle) =>
			r.area == (r.width * r.height)
		})
	}
	property("A rectangle should be able to identify which rectangle is bigger") {
		check(forAll { (r1:Rectangle, r2:Rectangle) =>
			(r1 biggerThan r2) == (r1.area > r2.area)
		})
	}
}