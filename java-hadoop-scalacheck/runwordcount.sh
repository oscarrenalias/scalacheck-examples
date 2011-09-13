#!/bin/sh
mvn clean package
rm -rf output
hadoop jar target/Java-Hadoop-ScalaCheck-1.0-SNAPSHOT.jar com.company.hadoop.WordCount input output
