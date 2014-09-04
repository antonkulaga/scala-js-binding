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

object Build extends sbt.Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

  override def rootProject = Some(preview)

  val sharedSrcDir = "scala"

  val semWebVersion =  "0.6.8"

  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file(".")).enablePlugins(PlayScala) settings(previewSettings: _*) dependsOn shared dependsOn bindingPlay aggregate frontend

  lazy val frontend = Project(
    id   = "frontend",
    base = file("frontend")
  ) dependsOn shared dependsOn binding

  lazy val models = Project(
    id = "models",
    base = file("models")
  )

  lazy val shared = Project(
    id = "shared",
    base = file("shared")
  ) settings (  sourceDirectory := (sourceDirectory in models).value )

  lazy val binding = Project(
    id = "binding",
    base = file("binding")
  ) dependsOn jsmacro  dependsOn models

  lazy val jsmacro = Project(
    id = "js-macro",
    base = file("jsmacro")
  )

  lazy val bindingPlay = Project(
    id = "binding-play",
    base = file("binding-play")
  ) dependsOn shared

  //lazy val sharedCode= unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "src" / "main" / "scala"

  lazy val previewSettings = Seq(

      ScalaJSKeys.relativeSourceMaps := true, //just in case as sourcemaps do not seem to work=(

      parallelExecution in Test := false,

      //scalajsOutputDir     := (crossTarget in Compile).value / "classes" / "public" / "javascripts",

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      //scalajsOutputDir     := (crossTarget in Compile).value / "classes" / "public" / "javascripts",

      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (frontend, Compile)),


      dist <<= dist dependsOn (fullOptJS in (frontend, Compile)),

      //test in Test <<= (test in Test) dependsOn (test in (binding, Test)),

      watchSources <++= (sourceDirectory in (frontend, Compile)).map { path => (path ** "*.scala").get}

    ) ++ (   Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, /*packageLauncher,*/ fastOptJS, fullOptJS) map { packageJSKey =>
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


  val sameSettings = bintraySettings ++Seq(

    organization := "org.denigma",

    version := "0.5.3",

    scalaVersion := "2.11.2",

    resolvers += Opts.resolver.repo("scalax", "scalax-releases"),

    resolvers += Opts.resolver.repo("alexander-myltsev", "maven"),

    // The Typesafe repository
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",


    resolvers +=  Resolver.url("scala-js-releases",
      url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
        Resolver.ivyStylePatterns),

    scalacOptions ++= Seq( "-feature", "-language:_" )

  )



  lazy val publishSettings = Seq(
    repository in bintray := "denigma-releases",

    bintrayOrganization in bintray := Some("denigma"),

    licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

    bintrayPublishIvyStyle := true
  )

  /**
   * For parts of the project that we will not publish
   */
  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

}
