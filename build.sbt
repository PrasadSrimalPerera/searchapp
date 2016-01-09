name := """searchapp"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)
libraryDependencies += "junit" % "junit" % "4.10"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1207"
libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
