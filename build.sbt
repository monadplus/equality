resolvers in Global += Resolver.sonatypeRepo("releases")

publishMavenStyle := false

lazy val shapelessVersion     = "2.3.3"
lazy val catsVersion          = "1.5.0"
lazy val scalaTestVersion     = "3.0.5"

lazy val commonDependencies = Seq(
  "com.chuusai"   %% "shapeless" % shapelessVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test
)

lazy val compilerFlags = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Xlint:_",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-extra-implicit",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-value-discard"
  )
)

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "monadplus",
  name := "equality",
  version := "0.0.1",
  scalaVersion := "2.12.8",
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  parallelExecution in Test := true,
  fork in Test := true,
  libraryDependencies ++= commonDependencies,
) ++ compilerFlags

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)