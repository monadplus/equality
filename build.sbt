resolvers in Global += Resolver.sonatypeRepo("releases")

lazy val contributors = Seq(
  "monadplus" -> "Arnau Abella"
)

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
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:higherKinds"
  ) ++ (if (scalaBinaryVersion.value.startsWith("2.12"))
          List(
            "-Xlint",
            "-Xfatal-warnings",
            "-Yno-adapted-args",
            "-Ywarn-value-discard",
            "-Ywarn-unused-import",
            "-Ypartial-unification"
          )
        else Nil),
  scalacOptions in (Test, compile) --= Seq(
    "-Ywarn-unused-import",
    "-Xlint",
    "-Xfatal-warnings"
  )
)

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "io.monadplus",
  scalaVersion := "2.12.8",
//  TODO: support 2.11.12 (tests are failing)
//  crossScalaVersions := Seq(scalaVersion.value, "2.11.12"),
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  parallelExecution in Test := true,
  fork in Test := true,
  scalafmtOnCompile := true,
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
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
  micrositeDescription := "Compare arbitrary ADTs",
  micrositeAuthor := "Arnau Abella",
  micrositeGithubOwner := "monadplus",
  micrositeGithubRepo := "equality",
  micrositeBaseUrl := "/equality",
  micrositeDocumentationUrl := "https://monadplus.github.io/equality",
  micrositeFooterText := None,
  micrositeHighlightTheme := "atom-one-light",
  micrositePalette := Map(
    "brand-primary"   -> "#3e5b95",
    "brand-secondary" -> "#294066",
    "brand-tertiary"  -> "#2d5799",
    "gray-dark"       -> "#49494B",
    "gray"            -> "#7B7B7E",
    "gray-light"      -> "#E5E5E6",
    "gray-lighter"    -> "#F4F3F4",
    "white-color"     -> "#FFFFFF"
  ),
  fork in tut := true,
  scalacOptions in Tut --= Seq(
    "-Xfatal-warnings",
    "-Ywarn-unused:imports",
    "-Ywarn-unused-import"
  )
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
  .settings(name := "equality-core")
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(coverageSettings)

lazy val docs = project
  .in(file("docs"))
  .settings(noPublishSettings)
  .settings(micrositeSettings)
  .settings(commonSettings)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(TutPlugin)
  .dependsOn(core)
