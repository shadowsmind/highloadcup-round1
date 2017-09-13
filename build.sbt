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
  scalacOptions ++= List("-unchecked", "-deprecation", "-encoding", "UTF8", "-feature")
)

val akkaHttpVersion       = "10.0.10"
val macWireVersion        = "2.3.0"
val typesafeConfigVersion = "1.3.1"
val pureConfigVersion     = "0.8.0"
val betterFilesVersion    = "3.1.0"
val hsqldbVersion         = "2.4.0"
val hikariCPVersion       = "2.7.1"
val flyWayVersion         = "4.2.0"
val slickVersion          = "3.2.1"

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
  "com.typesafe.slick"       %% "slick"                % slickVersion
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "highloadcup-r1",
    libraryDependencies ++= dependencies
  )
  .enablePlugins(DockerPlugin)

dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(
    sys.error("Expected exactly one main class"))
  val jarTarget = s"/highloadcup/${jarFile.getName}"
  val classpathString = classpath.files.map("/highloadcup/" + _.getName).mkString(":") + ":" + jarTarget
  new Dockerfile {
    from("openjdk:8")
    add(classpath.files, "/highloadcup/")
    add(jarFile, jarTarget)
    expose(80)
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

imageNames in docker := Seq(ImageName("stor.highloadcup.ru/travels/strong_catfish"))