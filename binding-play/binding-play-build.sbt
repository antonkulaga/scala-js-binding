import sbt.Keys._

Build.sameSettings

name := "binding-play"

resolvers += Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.4",
  "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",
  "com.softwaremill.macwire" %% "macros" % Build.macwireVersion,
  "com.softwaremill.macwire" %% "runtime" % Build.macwireVersion
)

bintraySettings

Build.publishSettings
