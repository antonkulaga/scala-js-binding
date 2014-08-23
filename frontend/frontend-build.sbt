import bintray.Opts
import sbt.Keys._

scalaJSSettings

Build.sameSettings

name := "frontend"

resolvers += Opts.resolver.repo("alexander-myltsev", "maven")

//persistLauncher := true

//persistLauncher in Test := false

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true
