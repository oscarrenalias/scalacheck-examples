#!/bin/sh
mvn clean package
hadoop jar target/Java-Hadoop-ScalaCheck-1.0-SNAPSHOT.jar com.company.hadoop.WordCount input output
