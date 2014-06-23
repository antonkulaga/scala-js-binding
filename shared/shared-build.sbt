import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin.bintraySettings
import bintray.Keys._


Build.sameSettings

name := "binding-models"

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies +=  "org.scalax" %% "semweb" % Build.semWebVersion

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/"


libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1"

libraryDependencies +=  "com.scalarx" %% "scalarx" % "0.2.5"

libraryDependencies +=  "com.lihaoyi" %% "utest" % "0.1.6" % "test"

Build.publishSettings