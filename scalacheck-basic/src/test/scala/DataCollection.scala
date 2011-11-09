package com.company.scalacheck

import org.scalacheck.Properties
import org.scalacheck.Prop._

object DataCollectionSpecification extends Properties("Data collection examples") {
	import RectangleGenerator._

	/**
	 * The Prop.collect method is the easiest way to group our test data, but depending on the classification
	 * criteria for our data, it is possible that data cannot be grouped in a meaningful way.
	 * In the following example, the console output will show a long list of pairs of
	 * height and width, each one of them with a 1% frequency
	 */
	property("data collection spec with Prop.collect") = forAll { (r:Rectangle) =>
		collect((r.width, r.height)) {
			r.areaCorrect == (r.width * r.height)
		}
	}

	/**
	 * Helper function to help us classify input data. Using a partfial function we can plug in some pattern
	 * matching with guards that define how our input data gets classified. The return value of the function
	 * is a string (this function always returns something) that is used by ScalaCheck as the grouping criteria
	 * for our data
	 */
	val collector: PartialFunction[Rectangle,String] = {
		case r if r.perimeter < 10000 => "small"
		case r if r.perimeter > 10000 && r.perimeter < 25000 => "medium"
		case r if r.perimeter > 25000 => "large"
	}

	/**
	 * This is the same check as the previous example, but now data is grouped using our collector
	 * custom function. The console output now is suddenly more meaningful for analysis
	 */
	property("data collection spec with Prop.collect and a grouping function") = forAll { (r:Rectangle) =>
		collect(collector(r)) {
			r.areaCorrect == (r.width * r.height)
		}
	}

	/**
	 * Here we use the "binary" version of Prop.classify to classify our random rectangle objects into "taller" or "wider",
	 * and then with Prop.collect we've collected the data using our previous method. The output now is a two-level
	 * grouping of our data.
	 * The example also shows how Prop.classify and Prop.collect can be combined within the same property check,
	 * and even multiple calls can be nested to obtain a more granular classification
	 */
	property("data collection spec with Prop.classify and Prop.collect") = forAll { (r:Rectangle) =>
		classify(r.height > r.width, "taller", "wider") {
			collect(collector(r)) {
				r.areaCorrect == (r.width * r.height)
			}
		}
	}
}