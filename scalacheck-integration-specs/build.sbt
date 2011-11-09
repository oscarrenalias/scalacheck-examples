name := "scalacheck-integration-specs"

organization := "com.accenture"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

// package dependencies
libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "1.5",
	"org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test"
)

// use the specs2 runner
testFrameworks += TestFrameworks.Specs2