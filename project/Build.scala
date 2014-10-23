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

trait Versions {
  val macwireVersion = "0.7.1"

  val semWebVersion =  "0.6.13"

  val bindingVersion = "0.6.4"

  val graphVersion = "0.6.4"

}

object Build extends sbt.Build with UniversalKeys with ModelsBuild with Versions{

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  override def rootProject = Some(preview)


  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file(".")).enablePlugins(PlayScala) settings(previewSettings: _*) dependsOn bindingPlay aggregate frontend

  lazy val frontend = Project(
    id   = "frontend",
    base = file("frontend")
  ) dependsOn  binding dependsOn graphs

  lazy val graphs = Project(
    id = "graphs",
    base = file("graphs")
  ) dependsOn (binding)


  lazy val binding = Project(
    id = "binding",
    base = file("binding")
  ) dependsOn (jsmacro, models_js)


  lazy val jsmacro = Project(
    id = "js-macro",
    base = file("jsmacro")
  )

  lazy val bindingPlay = Project(
    id = "binding-play",
    base = file("binding-play")
  ) dependsOn models_jvm aggregate models_jvm

  //lazy val sharedCode= unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "src" / "main" / "scala"

  lazy val previewSettings = Seq(

      ScalaJSKeys.relativeSourceMaps := true, //just in case as sourcemaps do not seem to work=(

      parallelExecution in Test := false,

      scalajsOutputDir := (classDirectory in Compile).value  / "public" / "javascripts",

      //scalajsOutputDir := (crossTarget in Compile).value / "classes"  / "public" / "javascripts",

      //scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (frontend, Compile)),

      dist <<= dist dependsOn (fullOptJS in (frontend, Compile)),

      stage <<= stage dependsOn (fullOptJS in (frontend, Compile)),

      //test in Test <<= (test in Test) dependsOn (test in (binding, Test)),

      watchSources <++= (sourceDirectory in (frontend, Compile)).map { path => (path ** "*.scala").get}

    ) ++ (   Seq( fastOptJS, fullOptJS) map { packageJSKey =>
      crossTarget in (frontend, Compile, packageJSKey) := scalajsOutputDir.value
    }
    )

  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }

  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (frontend, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }

}
