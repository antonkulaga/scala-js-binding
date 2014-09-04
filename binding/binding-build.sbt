import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import bintray.Plugin._

scalaJSSettings

Build.sameSettings

name := "binding"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

ScalaJSKeys.persistLauncher := true

ScalaJSKeys.persistLauncher in Test := false

resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
