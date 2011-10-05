package com.company.hadoop.tests

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import java.lang.Long
import org.apache.hadoop.mrunit.types.Pair
import com.company.hadoop.WordCount.{Map, Reduce}
import org.apache.hadoop.mrunit.mapreduce.{MapDriver, ReduceDriver}
import org.apache.hadoop.io.{LongWritable, IntWritable, Text}

/**
 * These are the generators and arbitrary objects that will generate random IntWritable and Text objects
 */
object HadoopGenerators {
	// simple generator of IntWritable objects
	val intWritableGen: Gen[IntWritable] = for {
	  num <- Gen.choose(0, 9999)
	} yield(new IntWritable(num))

	// generator of text objects (as simple words, which may be blank)
	val textGen: Gen[Text] = for {
		text <- Gen.alphaStr
	} yield(new Text(text))

	// generator of LongWritable objects, with an upper limit for the value
	def longWritableGen(upperRange:Int): Gen[LongWritable] = for {
		num <- Gen.choose(0, upperRange)
	} yield(new LongWritable(num))

	// generator of lists of IntWritables
	val intWritableListGen: Gen[List[IntWritable]] = Gen.listOf(intWritableGen)
	// arbitrary generators for the ones above
	implicit val arbIntWritableListGen: Arbitrary[List[IntWritable]] = Arbitrary(intWritableListGen)
	val arbTextListGen: Arbitrary[List[Text]] = Arbitrary(Gen.listOf(textGen))
	// arbitrary generator for Text objects
	implicit val arbText: Arbitrary[Text] = Arbitrary(textGen)
	// arbitrary generator for LongWritable objects
	implicit val arbLongWritable: Arbitrary[LongWritable] = Arbitrary(longWritableGen(99999))

	// this is the key generator for the mapper tests: it generates a tuple where the first element is a list of random words
	// for the mapper, while the second element is the actual number of words in the text; the idea here is that in the
  // verification of the property, we only need to make sure that the mapper generated a list of words whose number of
  // elements is exactly the same one as calculated in the generator (which we already know is the correct length)
  val textLineGenWithCount: Gen[(Text,Int)] = for {
    words <- Gen.listOf(textGen)  // generates a line of strings
  } yield((new Text(words.mkString(" ")), words.filter(_.toString.trim != "").size))
  // the second part of the yield() clause above is neede because textGen may generate empty words, which are ignored
  // the mapper, so we need to make sure that when counting the correct number of words, we ignore the empty ones
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

	// convert from MRUnit's Pair to a tuple for easier handling
	implicit def Pair2Tuple[U,T](p:Pair[U,T]):Tuple2[U,T] = (p.getFirst, p.getSecond)
}

object WordCountSpecification extends Properties("Mapper and reducer tests") {


	import scala.collection.JavaConversions._   // import our generators and implicit conversions
  import HadoopImplicits._    // required for Scala<->Hadoop type conversions
  import HadoopGenerators._   // import our arbitrary generators into the scope

	// create mapper and reducers
	val reducer = new Reduce
	val mapper = new Map

  // this is used later in some of the comparisons
  case object one extends IntWritable(1)

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

    // The result of the processing will be true if results.headOption returned a None, because we need to account
    // for empty lines (which would not generate an key,value output from the mapper)
		results.headOption.map(pair => pair._1 == value && pair._2 == one).getOrElse(true)
	}

	property("The mapper correctly maps lines with multiple words") =
    forAll(longWritableGen(99999), textLineGenWithCount) { (key:LongWritable, values:(Text,Int)) =>
				val driver = new MapDriver(mapper)

				// collect and print data based on the number of words in the input string
				collect("Number of words = " + values._2) {
					driver.withInput(key, values._1)
					val results = driver.run

					(results.forall(one == _._2) &&   // checks that for all pairs of (key,value), the value is "1" and...
                                            // here _ is a shorthand to the pairs generated by the mapper
            results.size == values._2)      // ensure that then total amount of pairs from the mapper is as expected
				}
    }
}

object TestRunner extends App {
	WordCountSpecification.check
}