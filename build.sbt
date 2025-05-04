ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

val catsVersion       = "2.13.0"
val catsEffectVersion = "3.6.1"
val http4sVersion     = "0.23.30"
val logbackVersion    = "1.5.18"
val fs2Version        = "3.12.0"

libraryDependencies ++= Seq(
  "org.typelevel"         %% "cats-core"            % catsVersion,
  "org.typelevel"         %% "cats-effect"          % catsEffectVersion,
  "org.http4s"            %% "http4s-dsl"           % http4sVersion,
  "org.http4s"            %% "http4s-ember-server"  % http4sVersion,
  "org.http4s"            %% "http4s-ember-client"  % http4sVersion,
  "org.http4s"            %% "http4s-circe"         % http4sVersion,
  "io.circe"              %% "circe-generic"        % "0.14.13",
  "io.circe"              %% "circe-parser"         % "0.14.13",
  "ch.qos.logback"         % "logback-classic"      % logbackVersion,
  "com.olvind.tui"        %% "tui"                  % "0.0.7",
  "co.fs2"                %% "fs2-core"             % fs2Version,
  "co.fs2"                %% "fs2-io"               % fs2Version,
  "co.fs2"                %% "fs2-reactive-streams" % fs2Version,
  "co.fs2"                %% "fs2-scodec"           % fs2Version,
  "org.scalactic"         %% "scalactic"            % "3.2.19",
  "org.scalatest"         %% "scalatest"            % "3.2.19" % "test",
  "org.scalamock"         %% "scalamock"            % "7.3.2"  % Test,
  "org.scalatest"         %% "scalatest"            % "3.2.19" % Test,
  "org.jline"              % "jline"                % "3.29.0",
  "org.jline"              % "jline-terminal-jansi" % "3.29.0",
  "com.github.pureconfig" %% "pureconfig"           % "0.17.9",
  "com.monovore"          %% "decline-effect"       % "2.5.0",
  "com.monovore"          %% "decline"              % "2.5.0"
)

// Compiler options
scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-deprecation",
  "-Wunused:imports",
  "-language:implicitConversions",
  "-Wnonunit-statement"
  // "-Xfatal-warnings"
)

lazy val root = (project in file("."))
  .settings(
    name := "cli-street-imagery"
  )

Test / scalacOptions ++= Seq(
  // Allow using -Wnonunit-statement to find bugs in tests without exploding from scalatest assertions
  "-Wconf:msg=unused value of type org.scalatest.Assertion:s",
  "-Wconf:msg=unused value of type org.scalamock:s"
)

assembly / mainClass := Some("com.streetascii.Main")
// without this sbt assembly contains about duplicate module-info.class files
assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.discard
  case PathList("META-INF", "versions", "9", "module-info.class") =>
    MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
