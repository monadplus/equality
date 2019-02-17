resolvers in Global += Resolver.sonatypeRepo("releases")

lazy val shapelessVersion = "2.3.3"
lazy val catsVersion      = "1.6.0"
lazy val scalaTestVersion = "3.0.5"

lazy val commonDependencies = Seq(
  "com.chuusai"   %% "shapeless" % shapelessVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
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
  scalafmtOnCompile := true,
  libraryDependencies ++= commonDependencies,
) ++ compilerFlags

lazy val publishSettings =
  Seq(
    publishMavenStyle := true,  
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

lazy val micrositeSettings = Seq(
  micrositeName := "equality",
  micrositeDescription := "Better triple equals",
  micrositeAuthor := "Arnau Abella",
  micrositeGithubOwner := "monadplus",
  micrositeGithubRepo := "equality",
  micrositeBaseUrl := "/equality",
  micrositeDocumentationUrl := "https://monadplus.github.io/equality",
  micrositeFooterText := None,
  micrositeHighlightTheme := "atom-one-light",
  micrositePalette := Map(
    "brand-primary" -> "#3e5b95",
    "brand-secondary" -> "#294066",
    "brand-tertiary" -> "#2d5799",
    "gray-dark" -> "#49494B",
    "gray" -> "#7B7B7E",
    "gray-light" -> "#E5E5E6",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"
  ),
  fork in tut := true,
  scalacOptions in Tut --= Seq(
    "-Xfatal-warnings",
    "-Ywarn-unused-import"
  ),
  libraryDependencies += "com.47deg" %% "github4s" % "0.19.0",
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := sys.env.get("GITHUB_TOKEN")
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
  .settings(noPublishSettings)
  .settings(micrositeSettings)
  .settings(commonSettings)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(TutPlugin)
  .dependsOn(core)
