package com.company.scalacheck

import org.scalacheck.Prop._
import org.scalacheck.{Properties, Gen}

object SimpleGenerator extends Properties("Simple sample generator") {
	// simple generator based on the example given in the ScalaCheck documentation
	// TODO: create a more suitable one?
	val myGen = for {
  		n <- Gen.choose(10,20)
  		m <- Gen.choose(2*n, 500)
	} yield (n,m)

	// simple function
	def myFunction(n1:Int, n2:Int) = n1 + n2

	property("Test property with generator") = forAll(myGen) {(n:(Int,Int)) =>
		n match {
			case (n1, n2) => (n1 + n2) == myFunction(n1,n2)
		}
	}
}

case class Rectangle(val width:Double, val height:Double) {
	// when the width is a multiple of 3, this will fail
	lazy val area =  if(width % 11 ==0) (width * 1.0001 * height) else (width * height)
	lazy val perimeter = (2*width) + (2*height)
}

/**
 * Specification with a Generator that is used to create case classes and verify the
 * data in them. Please note that we can create a generator that will create case classes
 * directly, so the property below can be specified in a more straightforward way.
 */
object RectangleSpecification extends Properties("Rectangle specification") {

	val widthHeightGen:Gen[(Double,Double)] = for {
		height <- Gen.choose(0,9999)
		width <- Gen.choose(0,9999)
	} yield((width,height))

	property("Test area") = forAll(widthHeightGen) { (measures:(Double,Double)) =>
		Rectangle(measures._1, measures._2).area == measures._1 * measures._2
	}
}