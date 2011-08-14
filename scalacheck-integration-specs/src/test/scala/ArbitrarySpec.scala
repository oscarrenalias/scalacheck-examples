package com.company.scalacheck

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.mutable._
import org.specs2.ScalaCheck

/**
 * Case class that we're going to test
 */
case class Rectangle(val width:Double, val height:Double) {
	lazy val area =  width * height
	lazy val perimeter = (2*width) + (2*height)
	def biggerThan(r:Rectangle) = (area > r.area)
}

/**
 * In this example, the arbitrary generator is kept in a separate object
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


class ArbitraryRectangleSpec extends Specification with ScalaCheck {
	import com.company.scalacheck.RectangleGenerator._

	"Rectangle" should {
		"correctly calculate its area" ! check { (r:Rectangle) =>
			r.area == r.width * r.height
		}
		"be able to identify which rectangle is bigger" ! check { (r1:Rectangle, r2:Rectangle) =>
			(r1 biggerThan r2) == (r1.area > r2.area)
		}
	}
}