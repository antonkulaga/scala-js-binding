import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin
import bintray.Plugin.bintraySettings
import bintray.Keys._

scalaJSSettings

Build.sameSettings

name := "js-macro"

version := "0.1.1"

resolvers +=  Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

ScalaJSKeys.relativeSourceMaps := true

libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.5"

libraryDependencies += "com.scalatags" %%% "scalatags" % "0.2.5"

libraryDependencies +=  "com.scalarx" %%% "scalarx" % "0.2.4"

//libraryDependencies += "com.scalatags" %% "scalatags" % "0.2.5-JS"
//
//libraryDependencies +=  "com.scalarx" %% "scalarx" % "0.2.4-JS"

bintraySettings

Build.publishSettings

