package com.company.hadoop.tests

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import com.company.hadoop.WordCount.Reduce
import org.mockito.Mockito._
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

/*object MapperSpecification extends Properties("Mapper tests") {

}*/

object ReducerSpecification extends Properties("Reducer tests") {

	// create an empty reducer that will be used for all tests
	val reducer = new Reduce

	// simple generator of IntWritable objects
	val intWritableGen: Gen[IntWritable] = for {
	  num <- Gen.choose(0, 9999)
	} yield(new IntWritable(num))

		val intWritableListGen: Gen[List[IntWritable]] = Gen.listOf(intWritableGen)
	implicit val arbIntWritableListGen: Arbitrary[List[IntWritable]] = Arbitrary(intWritableListGen)

	property("data is correctly aggregated") = forAll { (values:List[IntWritable]) =>
		// this imports some built-in implicit conversions into the scope so that we can use
		// a Scala iterable such as List in places where java.lang.Iterable would be otherwise required, as
		// in this case
		import scala.collection.JavaConversions._

		//type ContextType = Reducer[Text, IntWritable, Text, IntWritable]
		//val context = mock(classOf[Reducer[Text,IntWritable,Text,IntWritable]#Context])
		//val context = mock(classOf[Reduce#Context])

		class DummyReducer extends Reducer[Text,IntWritable,Text,IntWritable] {
			type ContextType = Reducer#Context
		}
		val context = mock(classOf[DummyReducer#ContextType])

		reducer.reduce(new Text("foo"), values, null)
		true
	}
}

object TestRunner extends App {
	ReducerSpecification.check
}