package com.company.hadoop.tests

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import com.company.hadoop.WordCount.Reduce
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver
import java.lang.Long
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mrunit.types.Pair

/**
 * These are the generators and arbitrary objects that will generate random IntWritable and Text objects
 */
object HadoopGenerators {
	// simple generator of IntWritable objects
	val intWritableGen: Gen[IntWritable] = for {
	  num <- Gen.choose(0, 9999)
	} yield(new IntWritable(num))

	val textGen: Gen[Text] = for {
		text <- Gen.alphaStr
	} yield(new Text(text))

	val intWritableListGen: Gen[List[IntWritable]] = Gen.listOf(intWritableGen)
	implicit val arbIntWritableListGen: Arbitrary[List[IntWritable]] = Arbitrary(intWritableListGen)

	implicit val arbText: Arbitrary[Text] = Arbitrary(textGen)
}

/**
 * This allows us to use Int, Long and String transparently in places where we IntWritable, LongWritable
 * and Text are required
 */
object HadoopImplicits {
	implicit def IntWritable2Int(x:IntWritable) = x.get
	implicit def Int2WritableInt(x:Int) = new IntWritable(x)
	implicit def LongWritable2Long(x:LongWritable) = x.get
	implicit def Long2LongWritable(x:Long) = new LongWritable(x)
	implicit def Text2String(x:Text) = x.toString
	implicit def String2Text(x:String) = new Text(x)

	// convert from MRUnit's Pair to a tuple
	implicit def Pair2Tuple[U,T](p:Pair[U,T]):Tuple2[U,T] = (p.getFirst, p.getSecond)
}

/*object MapperSpecification extends Properties("Mapper tests") {

}*/

object ReducerSpecification extends Properties("Reducer tests") {

	// required for Java<->Scala implicit conversions, since it's always nicer to use Scala's List instead of Java's
	import scala.collection.JavaConversions._

	// required for Scala<->Hadoop type conversions
	import HadoopImplicits._

	// import our arbitrary generators into the scope
	import HadoopGenerators._

	// create an empty reducer that will be used for all tests
	val reducer = new Reduce

	property("data is correctly aggregated") = forAll { (key:Text, values:List[IntWritable]) =>
		// mrunit driver - it's more convenient to use mrunit as it will automatically set up our reduce contexts, which
		// otherwise are not so easy to create directly
		val driver = new ReduceDriver(reducer)

		// group input data based on the length of the list
		collect("key list length = " + values.length) {
			// set up the input and expected output values based on ScalaTest's random data
			driver.withInput(key, values)
			val results = driver.run

			// The reducer generates at most one key,value pair for each input key,value pair, but it may generate
			// nothing for empty keys so we need to be careful with that. List.headOption will return None if the list is empty,
			// and in that case map() won't be applied (can only be used with non-empty lists) and it will evaluate to true
			// straight away. In the list had a (key,value) pair in it, we can extract the second element from the tuple
			// with _2 and then "unbox" its Int value (otherwise it won't work)

			results.headOption.map(_._2.get == values.foldLeft(0)((x,total) => x + total)).getOrElse(true) == true

			// the same code above can be written in less concise way
			/*results.headOption match {
				case None => true
				case Some(sum) => sum._2.get == values.foldLeft(0)((x,total) => x + total)
				}
			}*/
		}
	}
}

object TestRunner extends App {
	ReducerSpecification.check
}