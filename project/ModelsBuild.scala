import sbt.Keys._
import sbt._
//import play.Keys._
import bintray.Opts
import bintray.Plugin.bintraySettings
import bintray.Keys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import play._
import play.Play._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._

import com.inthenow.sbt.scalajs.SbtScalajs
import com.inthenow.sbt.scalajs.SbtScalajs._
//import com.typesafe.sbt.SbtScalariform.defaultScalariformSettings
import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._

trait ModelsBuild extends BasicBuild
{
  self:sbt.Build with UniversalKeys=>

  lazy val modelsSettings = sameSettings ++publishSettings 

  /** `banana`, the root project. */
  lazy val models = Project(
    id = "models",
    base = file("models")
  ).settings(modelsSettings:_*).dependsOn(models_js, models_jvm).aggregate(models_js, models_jvm)

  lazy val modelsJsSettings =  modelsSettings ++ scalajsJsSettings ++ Seq(
    ScalaJSKeys.relativeSourceMaps := true,
    ScalaJSKeys.persistLauncher := true,
    ScalaJSKeys.persistLauncher in Test := false,
    libraryDependencies +=  "org.scalax" %%% "semweb" % Build.semWebVersion,
    libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1",
    libraryDependencies +=  "com.scalarx" %%% "scalarx" % "0.2.6"
  )

  /** `models_js`, a js only meta project. */
  lazy val models_js = Project(
    id = "models_js",
    base = file("models/js")
  ).settings(modelsJsSettings ++ linkedSources(models_jvm):_*).enablePlugins(SbtScalajs)

  lazy val modelsJvmSettings =  modelsSettings ++ scalajsJvmSettings  ++ Seq(
    libraryDependencies +=  "org.scalax" %% "semweb" % Build.semWebVersion,
    libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",
    libraryDependencies +=  "com.scalarx" %% "scalarx" % "0.2.6"
  )

  lazy val models_jvm = Project(
    id = "models_jvm",
    base = file("models/jvm")
  ).settings(modelsJvmSettings:_*)

}
