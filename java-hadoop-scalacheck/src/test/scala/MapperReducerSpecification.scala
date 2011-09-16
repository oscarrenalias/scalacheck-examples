package com.company.hadoop.tests

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import java.lang.Long
import org.apache.hadoop.mrunit.types.Pair
import org.apache.hadoop.io.{LongWritable, IntWritable, Text}
import com.company.hadoop.WordCount.{Map, Reduce}
import org.apache.hadoop.mrunit.mapreduce.{MapDriver, ReduceDriver}

/**
 * These are the generators and arbitrary objects that will generate random IntWritable and Text objects
 */
object HadoopGenerators {
	// simple generator of IntWritable objects
	val intWritableGen: Gen[IntWritable] = for {
	  num <- Gen.choose(0, 9999)
	} yield(new IntWritable(num))

	// generator of text objects
	val textGen: Gen[Text] = for {
		text <- Gen.alphaStr
	} yield(new Text(text))

	// generator of LongWritable objects, with an upper limit for the value
	def longWritableGen(upperRange:Int): Gen[LongWritable] = for {
		num <- Gen.choose(0, upperRange)
	} yield(new LongWritable(num))

	// geneerator of lists of IntWritable and Text
	val intWritableListGen: Gen[List[IntWritable]] = Gen.listOf(intWritableGen)
	val textListGen: Gen[List[Text]] = Gen.listOf(textGen)
	// arbitrary generators for the ones above
	implicit val arbIntWritableListGen: Arbitrary[List[IntWritable]] = Arbitrary(intWritableListGen)
	val arbTextListGen: Arbitrary[List[Text]] = Arbitrary(textListGen)
	// arbitrary generator for Text objects
	implicit val arbText: Arbitrary[Text] = Arbitrary(textGen)
	// arbitrary generator for LongWritable objects
	implicit val arbLongWritable: Arbitrary[LongWritable] = Arbitrary(longWritableGen(99999))

	// generator of random texts with multiple words joined by blank spaces
	val textLineGen: Gen[Text] = for {
		words <- textListGen
	} yield(new Text(words.mkString(" ")))
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

object WordCountSpecification extends Properties("Mapper and reducer tests") {

	// import our generators and implicit conversions
	import scala.collection.JavaConversions._

	// required for Scala<->Hadoop type conversions
	import HadoopImplicits._

	// import our arbitrary generators into the scope
	import HadoopGenerators._

	// create mapper and reducers
	val reducer = new Reduce
	val mapper = new Map

	property("The reducer correctly aggregates data") = forAll { (key:Text, values:List[IntWritable]) =>
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

	property("The mapper correctly maps single words") = forAll {(key:LongWritable, value:Text) =>
		val driver = new MapDriver(mapper)

		// we only need to verify that for input strings containing a single word, the mapper always returns that single word
		driver.withInput(key, value)
		val results = driver.run

		results.headOption.map(_._1 == value).getOrElse(true) == true
	}

	property("The mapper correctly maps lines with multiple words") = forAll(longWritableGen(99999), textLineGen) {
		// TODO: implement the required check logic
		(key:LongWritable, value:Text) =>
			val driver = new MapDriver(mapper)

			// collect data based on the number of words in the input string
			collect("Number of words = " + value.split(" ").size) {
				driver.withInput(key, value)
				val results = driver.run

				//results.headOption.map(_._1 == value).getOrElse(true) == true

				true
			}
	}
}

object TestRunner extends App {
	WordCountSpecification.check
}