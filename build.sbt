name := """blockchain-legal"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "java-ipfs-api-mvn-repo" at "https://raw.github.com/pascr/java-ipfs-api/mvn-repo/",
  "adridadou-bintray" at "https://dl.bintray.com/cubefriendly/maven/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.ipfs" % "ipfs" % "0.4.3.1",
  "org.parboiled" %% "parboiled" % "2.1.3",
  "org.yaml" % "snakeyaml" % "1.17",
  "org.scalatest" %% "scalatest" % "3.0.0-RC1" % "test",
  "io.reactivex" %% "rxscala" % "0.26.2",
  "org.adridadou"  % "eth-contract-api" % "0.12-SNAPSHOT",

  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "react" % "15.2.1",
  "org.webjars.npm" % "select2" % "4.0.3",
  "org.webjars.bower" % "dropzone" % "4.3.0",
  "org.webjars" % "jquery" % "3.0.0"
)
