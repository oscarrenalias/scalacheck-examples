package com.company.junit

import org.junit.Assert._
import org.junit.Test
import com.company.scalacheck.Rectangle
import org.scalacheck.{Prop, Arbitrary, Gen}
import org.scalacheck.{Test => SchkTest}

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

class ScalaJUnitTest {

	import RectangleGenerator._

	@Test def simpleTest = {
		val r = Rectangle(4, 5)
		assertTrue("Area does not match", r.areaCorrect == (4 * 5))
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

	/**
	 * Two properties for our testing purposes
	 */
	val failedTest = Prop.forAll { (r: Rectangle) =>
		r.area == (r.height * r.width)
	}
	val validTest = Prop.forAll { (r: Rectangle) =>
		r.areaCorrect == (r.height * r.width)
	}

	/**
	 * This property should not hold, and it should be reported as a JUnit assertion failure
	 */
	@Test def testWithFailedPropertyCheck = {
		// TODO: Test.check(Prop) is deprecated!
		assertTrue(SchkTest.check(failedTest).passed)
	}

	/**
	 * This property check holds true, and it should show as a successful test execution in JUnit
	 */
	@Test def testWithValidPropertyCheck = {
		assertTrue(SchkTest.check(validTest).passed)
	}

	/**
	 * With this implicit conversion we can pass a Prop object (such as the result of a Prop.forAll call) to
	 * JUnit's assertTrue
	 */
	implicit def prop2Boolean(p: Prop): Boolean = SchkTest.check(p).passed

	@Test def testWithImplicitConversion = {
		assertTrue(validTest)
	}
}