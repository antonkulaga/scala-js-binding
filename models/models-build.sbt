import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys

scalaJSSettings

Build.sameSettings

name := "binding-models"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies +=  "org.scalax" %%% "semweb" % Build.semWebVersion

libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1"

libraryDependencies +=  "com.scalarx" %%% "scalarx" % "0.2.5"

libraryDependencies +=  "com.lihaoyi" %% "utest" % "0.1.6" % "test"

Build.publishSettings

testFrameworks += new TestFramework("utest.runner.JvmFramework")