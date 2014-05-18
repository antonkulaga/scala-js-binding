import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin._
import bintray.Keys._

scalaJSSettings

Build.sameSettings

name := "binding"

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

version := "0.1"

libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-jquery" % "0.3"

libraryDependencies += "org.scala-lang.modules.scalajs" %% "scalajs-dom" % "0.4"

libraryDependencies +=  "org.scalajs" %% "scalajs-pickling" % "0.2"

libraryDependencies += "com.scalatags" %% "scalatags" % "0.2.5-JS"

libraryDependencies +=  "com.scalarx" %% "scalarx" % "0.2.4-JS"

libraryDependencies +=  "org.scalax" %% "semweb" % (Build.semWebVersion + "-JS")


(loadedTestFrameworks in Test) := {
  (loadedTestFrameworks in Test).value.updated(
    sbt.TestFramework(classOf[utest.runner.JsFramework].getName),
    new utest.runner.JsFramework(environment = (scalaJSEnvironment in Test).value)
  )
}

autoCompilerPlugins := true

bintraySettings

Build.publishSettings
