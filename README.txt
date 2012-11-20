Example code used in the ScalaCheck cookbook deliverable.

All the code used in this document is currently available in the Innersource repository: http://www.slideshare.net/oscarrenalias/scalacheck-cookbook-v10.

The code is structured in folders based on the same structure used throughout this document. Examples where Java and Scala code are mixed (scalacheck-basic, java-scalacheck, scalacheck-integration-junit) require Maven 2.x or 3.x to build and run. Pure Scala examples (scalacheck-integration-specs, scalacheck-integration-scalatest) require Simple Build Tool version 0.11 or greater.

There is no top-level project at the root folder above the sub-folders, so each project is an independent unit by itself.

scalacheck-basic
----------------
This folder contains basic ScalaCheck examples, showing most of its basic features (basic property checks, data grouping, conditional properties).

In order to run, switch to the project�s subfolder and execute following command:

  mvn scala:run -Dlauncher=test
  
java-scalacheck
---------------  
Integration of ScalaCheck property checks with Java code

In order to run, switch to the project�s subfolder and execute following command:

  mvn scala:run -Dlauncher=test
 
scalacheck-integration-scalatest
--------------------------------
Examples of integration of ScalaTest with with ScalaCheck. Requires SBT.	

In order to run, switch to the project�s subfolder and execute following command:

  sbt reload test

scalacheck-integration-specs
----------------------------
Examples of integration of Specs with ScalaCheck. Requires SBT.	

In order to run, switch to the project�s subfolder and execute following command:

  sbt reload test

scalacheck-integration-junit 	
----------------------------
Examples of integration of ScalaCheck with JUnit, as well as JUnit support traits and a JUnit 4 runner for ScalaCheck tests written as Properties classes.

This example uses the Maven JUnit test runner (Surefire plugin) to run.	

In order to run, switch to the project�s subfolder and execute following command:

  mvn test

java-hadoop-scalacheck
----------------------
Code and ScalaCheck test cases that test Hadoop�s WordCount example.

In order to run, switch to the project�s subfolder and execute following command:

  mvn scala:run -Dlauncher=test