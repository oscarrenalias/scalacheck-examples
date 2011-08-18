package com.company.scalacheck

object Runner {
	def main(args: Array[String]) = {
		// run the properties
		SimpleProperties.run
		BiggerSpecification.check	// holds
		SecondSpecification.check	// holds
		GroupedSpecifications.check	// holds
		SimpleGenerator.check		// holds?
		RectangleSpecification.check // holds?
		ArbitraryRectangleSpecification.check // holds?
		DataCollectionSpecification.check // shows random data collection
	}
}