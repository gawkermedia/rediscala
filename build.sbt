import sbt.Tests.{InProcess, Group}

ThisBuild / version := "1.9.0-kinja-pekko"

val pekkoVersion = "1.0.2"

val pekkoActor = "org.apache.pekko" %% "pekko-actor" % pekkoVersion

val pekkoTestkit = "org.apache.pekko" %% "pekko-testkit" % pekkoVersion

val specs2 = "org.specs2" %% "specs2-core" % "4.8.0"

val stm = "org.scala-stm" %% "scala-stm" % "0.9.1"

val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.2"

val rediscalaDependencies = Seq(
  pekkoActor,
  stm,
  pekkoTestkit % "test",
  //scalameter % "test",
  specs2 % "test",
  scalacheck % "test"
)


val baseSourceUrl = "https://github.com/etaty/rediscala/tree/"

val Scala213 = "2.13.13"

lazy val standardSettings = Def.settings(
  name := "rediscala",
  organization := "com.github.etaty",
  scalaVersion := Scala213,
  crossScalaVersions := Seq(Scala213),

  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/etaty/rediscala")),
  scmInfo := Some(ScmInfo(url("https://github.com/etaty/rediscala"), "scm:git:git@github.com:etaty/rediscala.git")),
  apiURL := Some(url("http://etaty.github.io/rediscala/latest/api/")),
  pomExtra := (
    <developers>
      <developer>
        <id>etaty</id>
        <name>Valerian Barbot</name>
        <url>http://github.com/etaty/</url>
      </developer>
    </developers>
    ),

  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-Xlint",
    "-deprecation",
    "-feature",
    "-language:postfixOps",
    "-unchecked"
  )
)

lazy val BenchTest = config("bench") extend Test

lazy val benchTestSettings = inConfig(BenchTest)(Defaults.testSettings ++ Seq(
  BenchTest / sourceDirectory := baseDirectory.value / "src/benchmark",
  //testOptions in BenchTest += Tests.Argument("-preJDK7"),
  BenchTest / testFrameworks := Seq(new TestFramework("org.scalameter.ScalaMeterFramework")),

  //https://github.com/sbt/sbt/issues/539 => bug fixed in sbt 0.13.x
  BenchTest / testGrouping := ((BenchTest / definedTests) map partitionTests).value
))

lazy val root = Project(id = "rediscala",
  base = file(".")
).settings(
  standardSettings,
  libraryDependencies ++= rediscalaDependencies
).configs(
  BenchTest
)

lazy val benchmark = {
  import pl.project13.scala.sbt.JmhPlugin

  Project(
    id = "benchmark",
    base = file("benchmark")
  ).settings(Seq(
    scalaVersion := Scala213,
    libraryDependencies += "net.debasishg" %% "redisclient" % "3.0"
  ))
    .enablePlugins(JmhPlugin)
    .dependsOn(root)
}

def partitionTests(tests: Seq[TestDefinition]) = {
  Seq(new Group("inProcess", tests, InProcess))
}
