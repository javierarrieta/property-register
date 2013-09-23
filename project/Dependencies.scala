import sbt._
object Dependencies {

  val sprayVersion = "1.2-20130912"
  val akkaVersion = "2.2.0"
  
  val resolutionRepos = Seq(
    "spray repo" at "http://repo.spray.io/",
    "spray nighties" at "http://nightlies.spray.io/",
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases",
    "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val sprayCan      = "io.spray"                                %   "spray-can"                   % sprayVersion
  val sprayRouting  = "io.spray"                                %   "spray-routing"               % sprayVersion
  val sprayUtil     = "io.spray"                                %   "spray-util"                  % sprayVersion
  val sprayHttp     = "io.spray"                                %   "spray-http"                  % sprayVersion
  val sprayHttpx    = "io.spray"                                %   "spray-httpx"                 % sprayVersion
  val sprayClient   = "io.spray"                                %   "spray-client"                % sprayVersion
  val scalaReflect  = "org.scala-lang"                          %   "scala-reflect"               % "2.10.2"
  val akkaActor     = "com.typesafe.akka"                       %%  "akka-actor"                  % akkaVersion
  val akkaSlf4j     = "com.typesafe.akka"                       %%  "akka-slf4j"                  % akkaVersion
  val akkaTestKit   = "com.typesafe.akka"                       %%  "akka-testkit"                % akkaVersion
  val logging       = "com.typesafe"                            %% "scalalogging-slf4j"           % "1.0.1"
  val parboiled     = "org.parboiled"                           %%  "parboiled-scala"             % "1.1.4"
  val shapeless     = "com.chuusai"                             %%  "shapeless"                   % "1.2.3"
  val scalatest     = "org.scalatest"                           %%  "scalatest"                   % "1.9.1"
  val specs2        = "org.specs2"                              %%  "specs2"                      % "1.12.3"
  val sprayJson     = "io.spray"                                %%  "spray-json"                  % "1.2.3"
  val twirlApi      = "io.spray"                                %%  "twirl-api"                   % "0.6.1"
  val clHashMap     = "com.googlecode.concurrentlinkedhashmap"  %   "concurrentlinkedhashmap-lru" % "1.3.2"
  val jettyWebApp   = "org.eclipse.jetty"                       %   "jetty-webapp"                % "8.1.8.v20121106"
  val servlet30     = "org.eclipse.jetty.orbit"                 %   "javax.servlet"               % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar")
  val logback       = "ch.qos.logback"                          %   "logback-classic"             % "1.0.9"
  val mimepull      = "org.jvnet.mimepull"                      %   "mimepull"                    % "1.9.1"
  val pegdown       = "org.pegdown"                             %   "pegdown"                     % "1.2.1"
  val liftJson      = "net.liftweb"                             %%  "lift-json" 
  val rMongo        = "org.reactivemongo"                       %% "reactivemongo"                % "0.10-SNAPSHOT"     // % "2.5-M4"
  val time          = "org.scalaj"                              %  "scalaj-time_2.9.1"            % "0.6"
}
