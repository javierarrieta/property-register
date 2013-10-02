resolvers ++= Seq(
  // for scala-ssh
  "spray repo" at "http://repo.spray.io",
  // sbt-twirl transitively depends on akka-actor right now...
  "typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.3.0")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.2")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.3.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-atmos" % "0.3.1")