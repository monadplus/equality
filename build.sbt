resolvers in Global += Resolver.sonatypeRepo("releases")

lazy val shapelessVersion = "2.3.3"
lazy val catsVersion      = "1.6.0"
lazy val scalaTestVersion = "3.0.5"

lazy val commonDependencies = Seq(
  "com.chuusai"   %% "shapeless" % shapelessVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
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
    "-deprecation",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Xfuture",
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
  ),
  scalacOptions in (Test, compile) --= Seq(
    "-Xfatal-warnings"
  )
)

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "io.monadplus",
  name := "equality",
  scalaVersion := "2.12.8",
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  parallelExecution in Test := true,
  fork in Test := true,
  libraryDependencies ++= commonDependencies
) ++ compilerFlags

lazy val publishSettings =
  Seq(
    bintrayOrganization := Some("io-monadplus"),
    bintrayPackage := "equality"
  )

lazy val noPublishSettings =
  Seq(
    skip in publish := true
  )

lazy val coverageSettings = 
  Seq(
    coverageMinimum := 70,
    coverageHighlighting := true,
    coverageFailOnMinimum := true
  )

lazy val equality = project
  .in(file("."))
  .aggregate(
    core,
    docs
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(coverageSettings)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(coverageSettings)
  .settings(name := "equality-core")

lazy val docs = project
  .in(file("docs"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    skip in publish := true
  )
  .enablePlugins(TutPlugin)
  .dependsOn(core)
