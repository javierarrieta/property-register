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
    .aggregate(records_common, records_import, records_rest)

  lazy val records_common = Project("records-common", file("records-common"))
    .settings(exampleSettings: _*)
    .settings(libraryDependencies ++=
    compile(rMongo, sprayJson, time) ++
      test(specs2, scalatest) ++
      provided(logback)
  )

  lazy val records_import = Project("records-import", file("records-import"))
    .settings(exampleSettings: _*)
    .settings(libraryDependencies ++=
      compile(akkaActor, sprayClient, logging) ++
      test(specs2, scalatest) ++
      provided(akkaSlf4j, logback)
    )
    .dependsOn(records_common)

  lazy val records_rest = Project("records-rest", file("records-rest"))
    .settings(exampleSettings: _*)
    .settings(libraryDependencies ++=
      compile(akkaActor, sprayCan, sprayRouting, sprayJson, logging) ++
      test(specs2, scalatest) ++
      provided(akkaSlf4j, logback)
    )
    .dependsOn(records_common)
}
