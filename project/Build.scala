import sbt._
import Keys._


object Build extends Build {
  import BuildSettings._
  import Dependencies._

  // configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Root Project
  // -------------------------------------------------------------------------------------------------------------------

  lazy val root = Project("root",file("."))
    .settings(basicSettings: _*)

  lazy val records_import = Project("records-import", file("records-import"))
    .settings(exampleSettings: _*)
    .settings(libraryDependencies ++=
      compile(akkaActor, rMongo) ++
      test(specs2) ++
      provided(akkaSlf4j, logback)
    )

}
