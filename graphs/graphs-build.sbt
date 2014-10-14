import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import bintray.Plugin._

scalaJSSettings

Build.sameSettings

version := Build.bindingVersion

name := "graphs"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

ScalaJSKeys.persistLauncher := true

ScalaJSKeys.persistLauncher in Test := false

resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/"

autoCompilerPlugins := true

bintraySettings

autoCompilerPlugins := true

Build.publishSettings