name := """blockchain-legal"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "java-ipfs-api-mvn-repo" at "https://raw.github.com/pascr/java-ipfs-api/mvn-repo/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.ipfs" % "api" % "0.0.1-SNAPSHOT",
  "org.parboiled" %% "parboiled" % "2.1.3",
  "org.yaml" % "snakeyaml" % "1.17",
  "org.scalatest" %% "scalatest" % "3.0.0-RC1" % "test",
  "org.adridadou"  % "eth-contract-api" % "0.5-SNAPSHOT",

  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "react" % "15.2.1",
  "org.webjars.npm" % "select2" % "4.0.3",
  "org.webjars.bower" % "dropzone" % "4.3.0",
  "org.webjars" % "jquery" % "3.0.0"
)
