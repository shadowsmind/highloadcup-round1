import com.typesafe.sbt.SbtScalariform.ScalariformKeys

ScalariformKeys.preferences := {
  import scalariform.formatter.preferences._
  FormattingPreferences()
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignArguments, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(DoubleIndentConstructorArguments, false)
}

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.shadowsmind",
  version := "0.1.0",
  scalaVersion := "2.12.3",
  scalacOptions ++= List("-unchecked", "-deprecation", "-encoding", "UTF8")
)

val akkaHttpVersion       = "10.0.10"
val macWireVersion        = "2.3.0"
val typesafeConfigVersion = "1.3.1"
val pureConfigVersion     = "0.8.0"
val betterFilesVersion    = "3.1.0"
val hsqldbVersion         = "2.4.0"
val hikariCPVersion       = "2.6.3"
val flyWayVersion         = "4.2.0"
val slickPgVersion        = "0.15.3"

val dependencies = Seq(
  "com.typesafe.akka"        %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka"        %% "akka-http-spray-json" % akkaHttpVersion,
  "com.softwaremill.macwire" %% "macros"               % macWireVersion % "provided",
  "com.typesafe"             %  "config"               % typesafeConfigVersion,
  "com.github.pureconfig"    %% "pureconfig"           % pureConfigVersion,
  "com.github.pathikrit"     %% "better-files"         % betterFilesVersion,
  "org.hsqldb"               %  "hsqldb"               % hsqldbVersion,
  "com.zaxxer"               %  "HikariCP"             % hikariCPVersion,
  "org.flywaydb"             %  "flyway-core"          % flyWayVersion,
  "com.github.tminglei"      %% "slick-pg"             % slickPgVersion,
  "com.github.tminglei"      %% "slick-pg_spray-json"  % slickPgVersion
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "highloadcup-r1",
    libraryDependencies ++= dependencies
  )