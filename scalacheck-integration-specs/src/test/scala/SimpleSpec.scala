package com.company.scalacheck

import org.specs2.mutable._
import org.specs2.ScalaCheck

/**
 * Basic Spec2 specification
 */
class HelloWorldSpec extends Specification {

  "The 'Hello world' string" should {
    "contain 11 characters" in {
      "Hello world" must have size (11)
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
  }
}

/**
 * Sample specification that uses a couple of simple ScalaCheck properties.
 * The properties are simplistic and rather useless
 */
class SimplePropertySpec extends Specification with ScalaCheck {
  "Strings" should {
    "append after each other with the concat method" ! check  { (a:String, b:String) =>
      a.concat(b) == a + b
    }
    "reverse correctly for non-empty strings" ! check {(a:String) =>
      // do it only for string lengths longer than 0, otherwise the property doesn't hold
      (a.length > 0) ==> (a.charAt(a.length-1) == a.reverse.charAt(0))
    }
  }
}