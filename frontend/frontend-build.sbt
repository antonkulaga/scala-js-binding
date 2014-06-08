import sbt.Keys._

scalaJSSettings

Build.sameSettings

name := "frontend"

//persistLauncher := true

//persistLauncher in Test := false

scalacOptions ++= Seq( "-feature", "-language:_" )

ScalaJSKeys.relativeSourceMaps := true
