import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin.bintraySettings
import bintray.Keys._


Build.sameSettings

name := "binding-models"

version := "0.4.4"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies +=  "org.scalax" %% "semweb" % Build.semWebVersion

libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3"



Build.publishSettings