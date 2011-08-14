import sbt._
import Process._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {	 
	val specsCore = "org.specs2" %% "specs2" % "1.5"
	val specsScalaz = "org.specs2" %% "specs2-scalaz-core" % "6.0.RC2" % "test"

	val releasesRepo = "releases" at "http://scala-tools.org/repo-releases"
	val snapshotsRepo = "snapshots" at "http://scala-tools.org/repo-snapshots"
	
	// use the specs2 runner
	def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
	override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
}