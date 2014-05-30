import sbt._
import Keys._
//import play.Keys._
import play._
import play.Play.autoImport._
import PlayKeys._

import scala.scalajs.sbtplugin.ScalaJSPlugin._
import scala.Some
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import Def.ScopedKey
import bintray.Plugin.bintraySettings
import bintray.Opts
import bintray.Keys._

object Build extends sbt.Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

  override def rootProject = Some(preview)

  val sharedSrcDir = "scala"

  val semWebVersion =  "0.3.1"

  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file(".")).enablePlugins(PlayScala) settings(previewSettings: _*) dependsOn shared aggregate frontend

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

  //lazy val sharedCode= unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "src" / "main" / "scala"

  lazy val previewSettings = Seq(

      ScalaJSKeys.relativeSourceMaps := true, //just in case as sourcemaps do not seem to work=(

      parallelExecution in Test := false,

      scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      compile in Compile <<= (compile in Compile) dependsOn (preoptimizeJS in (frontend, Compile)),

      test in Test <<= (test in Test) dependsOn (test in (binding, Test)),

      //sharedCode,

      dist <<= dist dependsOn (optimizeJS in (frontend, Compile)),

      watchSources <++= (sourceDirectory in (frontend, Compile)).map { path => (path ** "*.scala").get},

      crossTarget in (frontend, Compile, packageExternalDepsJS) := scalajsOutputDir.value,

      crossTarget in (frontend, Compile, packageInternalDepsJS) := scalajsOutputDir.value,

      crossTarget in (frontend, Compile, packageExportedProductsJS) := scalajsOutputDir.value,

      crossTarget in (frontend, Compile, preoptimizeJS) := scalajsOutputDir.value,

      crossTarget in (frontend, Compile, optimizeJS) := scalajsOutputDir.value

    )


  val sameSettings = bintraySettings ++Seq(

    organization := "org.denigma",

    scalaVersion := "2.10.4",

    resolvers += Opts.resolver.repo("scalax", "scalax-releases"),

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
