import com.typesafe.sbt.web.SbtWeb
import playscalajs.ScalaJSPlay.autoImport._
import playscalajs.{PlayScalaJS, ScalaJSPlay}
import sbt.Project.projectToRef
import sbt.Keys._
import sbt._
import bintray.Plugin._
import bintray.Keys._
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.packager.universal.UniversalKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.typesafe.sbt.web.SbtWeb.autoImport._
import playscalajs.PlayScalaJS.autoImport._

import org.scalajs.sbtplugin.cross.CrossProject

import play._
import play.twirl.sbt.Import.TwirlKeys


object BindingBuild extends sbt.Build with UniversalKeys {

  lazy val clients = Seq(frontend/*,binding,modelsJs*/)

  override def rootProject = Some(preview)

  lazy val frontEndSettings = sameSettings ++ Seq(
    version := Versions.bindingVersion,

    name := "frontend",

    scalacOptions ++= Seq( "-feature", "-language:_" ),

    persistLauncher := true,

    persistLauncher in Test := false,

    sourceMapsDirectories += binding.base / "..",

    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
  )

  lazy val frontend = Project(
    id   = "frontend",

    base = file("frontend"),

    settings = frontEndSettings


  ) enablePlugins(ScalaJSPlugin, ScalaJSPlay)  dependsOn binding

  lazy val bindingSettings = sameSettings++publishSettings ++ Seq(
    version := Versions.bindingVersion,

    name := "binding",

    libraryDependencies ++= Dependencies.binding.value,

    resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")
  )

  lazy val binding = Project(
    id = "binding",
    base = file("binding"),
    settings = bindingSettings
  )  enablePlugins ScalaJSPlugin dependsOn (jsmacro, modelsJs)


  lazy val modelsJsSettings =  sameSettings ++ publishSettings++ Seq(
    libraryDependencies ++= Dependencies.models_js.value
  )
  lazy val modelsJvmSettings =  sameSettings ++ publishSettings ++ Seq(
    libraryDependencies ++= Dependencies.models_jvm.value
  )
  lazy val models = CrossProject("models",new File("models"),CrossType.Full).
    settings(sameSettings: _*).
    jsSettings(modelsJsSettings: _* ).
    jvmSettings(modelsJvmSettings: _* )

  lazy val modelsJs = models.js
  lazy val modelsJvm   = models.jvm


  lazy val jsMacroSettings = sameSettings++ publishSettings ++ Seq(
    name := "js-macro",

    version := Versions.jsmacroVersion,

    libraryDependencies ++= Dependencies.macro_js.value,

    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

  )

  lazy val jsmacro = Project(
    id = "js-macro",
    base = file("jsmacro"),
    settings = jsMacroSettings
  ) enablePlugins ScalaJSPlugin

  lazy val bindingPlaySettings = sameSettings ++ bintraySettings ++ publishSettings ++ Seq(
    name := "binding-play",

    version := Versions.bindingPlayVersion,

    libraryDependencies ++= Dependencies.bindingPlay.value
  )

  lazy val bindingPlay = Project(
    id = "binding-play",
    base = file("binding-play"),
    settings = bindingPlaySettings
  ) dependsOn modelsJvm

  lazy val previewSettings = sameSettings ++ Seq(
      name := """binding-preview""",

      version := Versions.bindingVersion,


      libraryDependencies ++= Dependencies.preview.value,

      includeFilter in (Assets, LessKeys.less) := "*.less",

      excludeFilter in (Assets, LessKeys.less) := "_*.less",

      pipelineStages := Seq(scalaJSProd,digest, gzip),

      scalaJSProjects := clients,

      TwirlKeys.templateImports += "org.denigma.endpoints._"

    )



  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file("."))
    .enablePlugins(PlayScala,SbtWeb,PlayScalaJS)
    .settings(previewSettings: _*)
    .settings(scalaJSProjects :=  clients)
    .dependsOn(bindingPlay)
    .aggregate(clients.map(projectToRef): _*)




  protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug


  lazy val sameSettings = bintraySettings ++Seq(

    organization := "org.denigma",

    version := Versions.mainVersion,

    scalaVersion := "2.11.6",

    resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),

    resolvers += sbt.Resolver.bintrayRepo("alexander-myltsev", "maven"),

    resolvers += sbt.Resolver.bintrayRepo("markatta", "markatta-releases"),

    resolvers += "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven",

    resolvers += Resolver.sonatypeRepo("snapshots"),

    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",

    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },

      // The Typesafe repository
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",

    scalacOptions ++= Seq( "-feature", "-language:_" ),

    parallelExecution in Test := false

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
