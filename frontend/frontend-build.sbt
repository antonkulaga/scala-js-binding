import bintray.Opts
import sbt.Keys._

scalaJSSettings

Build.sameSettings

version := Build.bindingVersion

name := "frontend"

resolvers += Opts.resolver.repo("alexander-myltsev", "maven")

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true

ScalaJSKeys.persistLauncher := true

ScalaJSKeys.persistLauncher in Test := false
