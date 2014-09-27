import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import bintray.Plugin._

scalaJSSettings

Build.sameSettings

version := Build.bindingVersion

name := "binding"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

ScalaJSKeys.persistLauncher := true

ScalaJSKeys.persistLauncher in Test := false

resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/"

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"

libraryDependencies += "org.scalajs" %%% "codemirror" % "4.5-0.1"

libraryDependencies += "org.scalajs" %%% "threejs" % "0.0.68-0.1.1"

libraryDependencies += "com.softwaremill.macwire" %% "macros" % Build.macwireVersion

libraryDependencies += "com.softwaremill.macwire" %% "runtime" % Build.macwireVersion

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
