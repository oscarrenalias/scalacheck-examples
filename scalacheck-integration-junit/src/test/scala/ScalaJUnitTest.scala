package com.company.junit

import org.junit.Assert._
import org.junit.Test
import com.company.scalacheck.Rectangle
import org.scalacheck.{Prop, Arbitrary, Gen}
import org.scalacheck.{Test=>SchkTest}

class ScalaJUnitTest {

	@Test def simpleTest = {
		val r = Rectangle(4, 5)
		assertTrue("Area does not match", r.areaCorrect == (4 * 5))
	}

	/**
	 * Generator of case objects for the Rectangle class, as well as an arbitrary generator
	 */
	object RectangleGenerator {
		// generator for the Rectangle case class
		val rectangleGen: Gen[Rectangle] = for {
			height <- Gen.choose(0, 9999)
			width <- Gen.choose(0, 9999)
		} yield (Rectangle(width, height))

		// Arbitrary generator of rectangles
		implicit val arbRectangle: Arbitrary[Rectangle] = Arbitrary(rectangleGen)
	}

	/**
	 * This test cases uses our generator as the source of our random test data; as opposed to a ScalaCheck property,
	 * we are only using 1 random value per test execution. Please refer to the source code of the Rectangle class for this
	 * scenario, where the area method contains a bug that calculates the wrong area if the width is divisible by 2. This
	 * issue would not be reported as a failure of a test case in about 33% of the test runs, while it would be systematically
	 * highlighted as a failure when using pure ScalaCheck code
	 */
	@Test def simpleTestWithGenerator = {
		import RectangleGenerator._
		rectangleGen.sample map (r => assertTrue("Area is not correct", r.area == (r.height * r.width))) getOrElse assertTrue(false)
	}

	@Test def testWithFailedPropertyCheck = {
		import RectangleGenerator._
		val failedTest = Prop.forAll { (r:Rectangle) =>
			r.area == (r.height * r.width)
		}

		// TODO: Test.check(Prop) is deprecated!
		assertTrue(SchkTest.check(failedTest).passed)
	}

	@Test def testWithValidPropertyCheck = {
		import RectangleGenerator._
		val validTest = Prop.forAll { (r:Rectangle) =>
			r.areaCorrect == (r.height * r.width)
		}

		assertTrue(SchkTest.check(validTest).passed)
	}

	/**
	 * With this implicit conversion we can pass a Prop object (such as the result of a Prop.forAll call) to
	 * JUnit's assertTrue
	 */
	implicit def prop2Boolean(p:Prop):Boolean = SchkTest.check(p).passed

	@Test def testWithImplicitConversion = {
		import RectangleGenerator._
		val validTest = Prop.forAll { (r:Rectangle) =>
			r.areaCorrect == (r.height * r.width)
		}

		assertTrue(validTest)
	}
}