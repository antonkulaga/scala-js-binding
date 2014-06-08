import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import bintray.Plugin._

scalaJSSettings

Build.sameSettings

name := "binding"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.5"

libraryDependencies +=  "org.scalax" %%% "semweb" % Build.semWebVersion

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
