name := "scalacheck-integration-scalatest"

organization := "com.accenture"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

// package dependencies
libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.6.1",
	"org.scala-tools.testing" %% "scalacheck" % "1.9"
)