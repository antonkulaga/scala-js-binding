import bintray._
import bintray.BintrayPlugin.autoImport._
import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.SbtWeb.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import play.PlayScala
import play.twirl.sbt.Import.TwirlKeys
import playscalajs.PlayScalaJS.autoImport._
import playscalajs.ScalaJSPlay.autoImport._
import playscalajs.{PlayScalaJS, ScalaJSPlay}
import sbt.Keys._
import sbt.Project.projectToRef
import sbt._


object BindingBuild extends sbt.Build with UniversalKeys {

  lazy val clients = Seq(frontend/*,semanticBinding,binding,modelsJs*/)

  override def rootProject = Some(preview)

  lazy val frontEndSettings = sameSettings ++ Seq(
    version := Versions.binding,

    name := "frontend",

    jsDependencies += RuntimeDOM % "test",

    sourceMapsDirectories += bindingJs.base / "..",

    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
  )

  lazy val frontend = Project(
    id   = "frontend",

    base = file("frontend"),

    settings = frontEndSettings


  ).enablePlugins(ScalaJSPlugin, ScalaJSPlay,BintrayPlugin)  dependsOn  semanticBinding

  lazy val semanticBindingSettings = sameSettings++publishSettings ++Seq(

    version := Versions.semanticBinding,

    name := "semantic-binding",

    libraryDependencies ++= Dependencies.semanticBinding.value
  )

  lazy val semanticBinding = Project(
    id = "semantic",
    base = file("semantic"),
    settings = semanticBindingSettings
  ).enablePlugins(ScalaJSPlugin,BintrayPlugin) dependsOn (bindingJs, modelsJs)


  lazy val modelsJsSettings =  sameSettings ++ publishSettings++ Seq(
    libraryDependencies ++= Dependencies.models_js.value
  )
  lazy val modelsJvmSettings =  sameSettings ++ publishSettings ++ Seq(
    libraryDependencies ++= Dependencies.models_jvm.value
  )
  lazy val models = CrossProject("models",new File("models"),CrossType.Full).
    settings(sameSettings: _*).
    jsSettings(modelsJsSettings: _* ).
    jvmSettings(modelsJvmSettings: _* ).
    enablePlugins(BintrayPlugin)

  lazy val modelsJs = models.js
  lazy val modelsJvm   = models.jvm.dependsOn(bindingJvm)


  lazy val bindingSharedSettings = sameSettings++publishSettings ++ Seq(
    version := Versions.binding,
    name := "binding",
    scalaVersion:=Versions.scala
  )

  lazy val bindingSettingsJS = bindingSharedSettings ++ Seq(

    libraryDependencies ++= Dependencies.bindingJS.value
  )

  lazy val bindingSettingsJVM = bindingSharedSettings ++ Seq(

    libraryDependencies ++= Dependencies.bindingJVM.value
  )


  lazy val binding = CrossProject("binding",new File("binding"),CrossType.Full).
    settings(bindingSharedSettings: _*).
    enablePlugins(BintrayPlugin).
    jsSettings(bindingSettingsJS: _* ).
    jvmSettings( bindingSettingsJVM: _* )
    .enablePlugins(BintrayPlugin)

  lazy val bindingJs = binding.js dependsOn (jsmacro)
  lazy val bindingJvm   = binding.jvm



  lazy val jsMacroSettings = sameSettings++ publishSettings ++ Seq(
    name := "js-macro",

    version := Versions.jsmacro,

    libraryDependencies ++= Dependencies.macro_js.value,

    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

  )

  lazy val jsmacro = Project(
    id = "js-macro",
    base = file("jsmacro"),
    settings = jsMacroSettings
  ).enablePlugins(ScalaJSPlugin,BintrayPlugin)

  lazy val bindingPlaySettings = sameSettings  ++ publishSettings ++ Seq(
    name := "binding-play",

    version := Versions.bindingPlay,

    libraryDependencies ++= Dependencies.bindingPlay.value
  )

  lazy val bindingPlay = Project(
    id = "binding-play",
    base = file("binding-play"),
    settings = bindingPlaySettings
  ).dependsOn(modelsJvm) enablePlugins BintrayPlugin


  lazy val previewSettings = sameSettings ++ Seq(
      name := """binding-preview""",

      version := Versions.binding,

      libraryDependencies ++= Dependencies.preview.value,

      includeFilter in (Assets, LessKeys.less) := "*.less",

      excludeFilter in (Assets, LessKeys.less) := "_*.less",

      pipelineStages := Seq(scalaJSProd,digest, gzip),

      resourceGenerators in Test <+= PlayScalaJS.copyMappings(scalaJSTest, WebKeys.public in Assets).map(_ => Seq[File]()),

      scalaJSProjects := clients,

      TwirlKeys.templateImports += "org.denigma.endpoints._"

    )



  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file("."))
    .enablePlugins(PlayScala,SbtWeb,PlayScalaJS)
    .settings(previewSettings: _*)
    .dependsOn(bindingPlay)
    .aggregate(clients.map(projectToRef): _*)




  protected lazy val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug


  lazy val sameSettings = Seq(

    organization := "org.denigma",

    version := Versions.main,

    scalaVersion := Versions.scala,

    resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),

    resolvers += sbt.Resolver.bintrayRepo("markatta", "markatta-releases"),

    resolvers += sbt.Resolver.bintrayRepo("pellucid", "maven"),

    resolvers += Resolver.sonatypeRepo("releases"),

    resolvers += Resolver.sonatypeRepo("snapshots"),

    resolvers += Resolver.bintrayRepo("inthenow","releases"),

    scalacOptions ++= Seq( "-feature", "-language:_" ),

    parallelExecution in Test := false,

    javaOptions in (Test,run) += "-Xmx4G",

    updateOptions := updateOptions.value.withCachedResolution(true)

  )


  lazy val publishSettings = Seq(
    bintrayRepository := "denigma-releases",

    bintrayOrganization := Some("denigma"),

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
