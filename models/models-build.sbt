import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys

scalaJSSettings

Build.sameSettings

name := "binding-models"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies +=  "org.scalax" %%% "semweb" % Build.semWebVersion

libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1"

Build.publishSettings