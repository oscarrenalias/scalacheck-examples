This class contains an example Map Reduce program implemented on Hadoop (based on the classic WordCount) and some example ScalaCheck generators for testing
the mapper and the reducer using random data.

Required libraries are: 

Hadoop 0.20
Cloudera Hadoop MrUnit 0.20.2-737

The code is built with Maven, and the properties can be run as follows:

mvn scala:run -Dlauncher=test