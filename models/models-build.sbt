import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin.bintraySettings
import bintray.Keys._

scalaJSSettings

Build.sameSettings

name := "binding-models"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies +=  "org.scalax" %%% "semweb" % Build.semWebVersion

libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3"

Build.publishSettings