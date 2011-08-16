import sbt._
import Process._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {	 
	val releasesRepo = "releases" at "http://scala-tools.org/repo-releases"
	val scalaTest = "org.scalatest" %% "scalatest" % "1.6.1"	
	val scalaCheck = "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"
}